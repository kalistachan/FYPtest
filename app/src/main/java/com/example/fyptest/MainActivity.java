package com.example.fyptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.fragments.PurchaseFragment;
import com.google.firebase.database.DatabaseReference;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    //private Toolbar maintoolbar;
    //will delete if not needed
    Toolbar toolbar;
    Fragment fragment;
    //List<Product> prodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*maintoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        maintoolbar.setLogo(R.drawable.ic_logo_132dp_x_59dp);*/
        // will delete if not needed
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        ImageButton homepagebutton = (ImageButton) findViewById(R.id.homepage);
        homepagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotohome);
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        loadFragment(new PurchaseFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Catching Value thrown from login
        SharedPreferences prefs = getSharedPreferences("IDs", MODE_PRIVATE);
        String id = prefs.getString("userID", "UNKNOWN");
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();

//nav_drawer components-----------------------------------------------------------------------------

        //account_header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .addProfiles(
                        new ProfileDrawerItem().withName(R.string.account_header_user).withEmail(R.string.account_header_email)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();
        //account_header

        new DrawerBuilder().withActivity(this);

        //Primary = bold items, Secondary = normal items
        SectionDrawerItem categories_header = new SectionDrawerItem().withName(R.string.drawer_section_categories);
        PrimaryDrawerItem item_main_1 = new PrimaryDrawerItem().withName(R.string.drawer_main_1).withSelectable(false);
        PrimaryDrawerItem item_main_2 = new PrimaryDrawerItem().withName(R.string.drawer_main_2).withSelectable(false);
        SecondaryDrawerItem item_secondary = new SecondaryDrawerItem().withName(R.string.drawer_secondary).withSelectable(false);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withSelectedItem(-1)
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        categories_header,
                        item_main_1,
                        item_main_2,
                        new DividerDrawerItem(),
                        item_secondary,
                        new SecondaryDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //do funky stuff with clicked items :D
                        //all switch cases are examples for future reference
                        switch (position) {
                            case 2:
                                startActivity(new Intent(MainActivity.this, registerActivity.class));
                                return true;
                            case 3:
                                startActivity(new Intent(MainActivity.this, loginActivity.class));
                                return true;
                        }
                        return false;
                    }
                })
                .build();
//nav_drawer components-----------------------------------------------------------------------------


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_purchase:
                    //  mTextMessage.setText(R.string.title_purchases);
                    fragment = new PurchaseFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_group:
                    // mTextMessage.setText(R.string.title_group);
                    fragment = new GroupFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_watchlist:
                    //mTextMessage.setText(R.string.title_watchlists);
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    //pushing userInfo out of current view
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
