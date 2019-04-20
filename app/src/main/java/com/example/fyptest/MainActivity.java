package com.example.fyptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.fyptest.fragments.AddProductFragment;
import com.example.fyptest.fragments.CategoriesFragment;
import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.MainScreen;
import com.example.fyptest.fragments.NotificationsFragment;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.fragments.PurchaseFragment;
import com.example.fyptest.fragments.WatchListFragment;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Fragment fragment;
    BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logosmall);


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new ProductListingFragment());

        //Set to view main screen on application start-up
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, new ProductListingFragment());
        ft.commit();

        //Catching Value thrown from login
        SharedPreferences prefs = getSharedPreferences("IDs", MODE_PRIVATE);
        String id = prefs.getString("userID", "UNKNOWN");
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();


//nav_drawer components-----------------------------------------------------------------------------
        //account_header
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withSelectionListEnabledForSingleProfile(true)
                .addProfiles(
                        new ProfileDrawerItem().withName("Insert username here").withEmail("$%& Points Earned")
                )
                .build();
        //account_header

        new DrawerBuilder().withActivity(this);

        //Primary = bold items, Secondary = normal items
        PrimaryDrawerItem item_categories = new PrimaryDrawerItem().withName("Categories").withSelectable(false).withIcon(R.drawable.ic_apps_black_24dp);
        PrimaryDrawerItem item_notifications = new PrimaryDrawerItem().withName("Notifications").withSelectable(false).withIcon(R.drawable.ic_notifications_black_24dp);
        PrimaryDrawerItem item_help_centre = new PrimaryDrawerItem().withName("Help Centre").withSelectable(false).withIcon(R.drawable.ic_help_outline_black_24dp);
        PrimaryDrawerItem item_logout = new PrimaryDrawerItem().withName("Logout").withSelectable(false);

        //create the drawer and remember the `Drawer` result object
        final Drawer result = new DrawerBuilder()
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        //item position corresponds to listing of items here (includes dividers, etc.)
                        item_categories,
                        item_notifications,
                        new DividerDrawerItem(),
                        item_help_centre,
                        new DividerDrawerItem(),
                        item_logout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //do funky stuff with clicked items :D
                        switch (position) {
                            case 1:
                                fragment = new CategoriesFragment();
                                loadFragment(fragment);
                                break;
                            case 2:
                                fragment = new NotificationsFragment();
                                loadFragment(fragment);
                                break;
                            case 4:
                                break;
                            case 6:
                                startActivity(new Intent(MainActivity.this, loginActivity.class));
                        }
                        return true;
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
                    fragment = new PurchaseFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_group:
                    fragment = new GroupFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_watchlist:
                    fragment = new WatchListFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;

                /*case R.id.homepage:
                    fragment = new MainScreen();
                    //pushing userInfo out of current view
                    loadFragment(fragment);
                    return true;*/
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

}
