package com.example.fyptest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.fragments.PurchaseFragment;
import com.google.firebase.database.DatabaseReference;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ContainerDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    Fragment fragment;
    //List<Product> prodList;

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
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        loadFragment(new PurchaseFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item_main = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_main);
        SecondaryDrawerItem item_secondary = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_secondary);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withSelectedItem(-1)
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item_main,
                        new DividerDrawerItem(),
                        item_secondary,
                        new SecondaryDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //do funky stuff with clicked items :D
                        return false;
                    }
                })
                .build();
//nav_drawer components-----------------------------------------------------------------------------
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
