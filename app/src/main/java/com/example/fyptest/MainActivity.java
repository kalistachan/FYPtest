package com.example.fyptest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.Adapters.CustomAdapter;
import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.CategoriesFragment;
import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.HelpCentreFragment;
import com.example.fyptest.fragments.NotificationsFragment;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.fragments.PurchaseFragment;
import com.example.fyptest.fragments.SearchFragment;
import com.example.fyptest.fragments.WatchListFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Fragment fragment;
    BottomNavigationView navigation;
    Context context;
    DatabaseReference dbProductGroup;
    List<String> watchListItem;
    SharedPreferences prefs;
    String id;
    AccountHeader headerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logosmall);
        watchListItem = new ArrayList<>();

        View homepage = toolbar.getChildAt(0);
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ProductListingFragment();
                loadFragment(fragment);
            }
        });

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new ProductListingFragment());

        //Set to view main screen on application start-up
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, new ProductListingFragment());
        ft.commit();

        //Catching Value thrown from login
        prefs = getSharedPreferences("IDs", MODE_PRIVATE);
        id = prefs.getString("userID", "UNKNOWN");
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> itemList) {
                if (!itemList.isEmpty()) {
                    for (final String item : itemList) {
                        DatabaseReference dbGD = FirebaseDatabase.getInstance().getReference("Group Detail");
                        dbGD.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(item)) {
                                    DatabaseReference dbGDAgain = FirebaseDatabase.getInstance().getReference("Group Detail").child(item);
                                    dbGDAgain.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean notifyUser = true;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(id)) {
                                                    notifyUser = false;
                                                }
                                            }
                                            if (notifyUser) {
                                                notificationTest(item);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });

//nav_drawer components-----------------------------------------------------------------------------
        //account_header

        //Log.d("custname", "value: " + custName);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimaryDark)
                .withSelectionListEnabledForSingleProfile(false)
                .build();
        //account_header
        getCusName(id);

        new DrawerBuilder().withActivity(this);

        //Primary = bold items, Secondary = normal items
        PrimaryDrawerItem item_notifications = new PrimaryDrawerItem().withName("Notifications").withSelectable(false).withIcon(R.drawable.ic_notifications_black_24dp);
        PrimaryDrawerItem item_help_centre = new PrimaryDrawerItem().withName("Help Centre").withSelectable(false).withIcon(R.drawable.ic_help_outline_black_24dp);
        PrimaryDrawerItem item_logout = new PrimaryDrawerItem().withName("Logout").withSelectable(false);

        //create the drawer and remember the `Drawer` result object
        final Drawer result = new DrawerBuilder()
                .withSelectedItem(-1)
                .withActivity(this)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        //item position corresponds to listing of items here (includes dividers, etc.)
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
                                fragment = new NotificationsFragment();
                                loadFragment(fragment);
                                break;
                            case 3:
                                fragment = new HelpCentreFragment();
                                loadFragment(fragment);
                                break;
                            case 5:
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.clear();
                                edit.apply();
                                startActivity(new Intent(context, loginActivity.class));
                                break;
                        }
                        return true;
                    }
                })
                .withCloseOnClick(true)
                .withDelayOnDrawerClose(1)
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Enter Product Name");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                TextView textView=(TextView)findViewById(R.id.action_search);
                textView.setText(newText);
                swapToSearchFragment(newText);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                TextView textView=(TextView)findViewById(R.id.action_search);
                textView.setText(query);

                swapToSearchFragment(query);
                return true;
            }

        };

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportFragmentManager().popBackStack();
                return false;
            }
        });

        menuItemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, "onMenuItemActionExpand called", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().popBackStack();
                Toast.makeText(MainActivity.this, "onMenutItemActionCollapse called", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        searchView.setOnQueryTextListener(queryTextListener);

        return true;

    }

    public void swapToSearchFragment(String queryText) {
        SearchFragment newSearchFragment = new SearchFragment();
        Bundle arguments = new Bundle();
        arguments.putString("query" , queryText);
        newSearchFragment.setArguments(arguments);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newSearchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getCusName(final String idPass) {
        final String[] custName = new String[1];
        final String[] loyaltyPts = new String[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Customer Information");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    if (productSnapshot.child("cus_ID").getValue().toString().equalsIgnoreCase(idPass)) {
                        custName[0] = productSnapshot.child("cus_fName").getValue().toString() + " " + productSnapshot.child("cus_lName").getValue().toString();
                        loyaltyPts[0] = productSnapshot.child("cus_loyaltyPoint").getValue().toString();
                        headerResult.addProfiles(
                                new ProfileDrawerItem().withName(custName[0]).withEmail(loyaltyPts[0] + " Loyalty Points Earned")
                        );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notificationTest(String productID) {
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID);
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String productName = dataSnapshot.child("pro_name").getValue().toString();
                NotificationManager notify = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification popUp = new Notification.Builder(context)
                        .setContentText("A product group for your watchlist item, " + productName + ", had been created!")
                        .setContentTitle("Join The Group Now!")
                        .setSmallIcon(R.drawable.ic_logo_v1)
                        .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                        .setLights(Color.WHITE, 3000, 3000)
                        .build();
                popUp.flags |= Notification.FLAG_AUTO_CANCEL;
                notify.notify(1, popUp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(List<String> itemList);
    }

    private void readData (final FirebaseCallback firebaseCallback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(id)) {
                    DatabaseReference dbAgain = FirebaseDatabase.getInstance().getReference("Watch List").child(id);
                    dbAgain.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            watchListItem.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                watchListItem.add(snapshot.child("wl_pro_ID").getValue().toString());
                            }
                            firebaseCallback.onCallback(watchListItem);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkProductGroupDuration() {
        DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group");
        dbProductGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("pg_dateEnd").getValue().toString().equalsIgnoreCase("")) {
                        final String productID = snapshot.child("pg_pro_ID").getValue().toString();
                        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID);
                        dbProduct.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int totalQty = Integer.parseInt(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                                int minAcceptedOrder = Integer.parseInt(dataSnapshot.child("pro_minOrderAccepted").getValue().toString());
                                int minTarget = 0; //Get the min quantity
                                calculateCurrentOrderedQuantity(productID, minTarget);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calculateCurrentOrderedQuantity(final String productID, final int minOrderQty) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                }
                if (counter != minOrderQty) {
                    dismissGroup(productID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void dismissGroup(String productID) {
        DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group").child(productID);
        DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);

        dbProductGroup.removeValue();
        dbGroupDetail.removeValue();
    }
}
