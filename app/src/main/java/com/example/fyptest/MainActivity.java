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
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.widget.BottomNavigationView;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.text.LoginFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.database.blacklistedCreditCardClass;
import com.example.fyptest.database.orderHistoryClass;
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
import com.mikepenz.iconics.utils.Utils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
                navigation.getMenu().setGroupCheckable(0, false, true);
                fragment = new ProductListingFragment();
                loadFragment(fragment);
            }
        });

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        navigation.getMenu().getItem(0).setCheckable(false);
        loadFragment(new ProductListingFragment());

        //Set to view main screen on application start-up
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, new ProductListingFragment());
        ft.commit();

        //Identifying User
        try {
            this.prefs = MainActivity.this.getSharedPreferences("IDs", MODE_PRIVATE);
            this.id = prefs.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            MainActivity.this.startActivity(new Intent(context, loginActivity.class));
        }

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
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.color4GBTheme)
                .withSelectionListEnabledForSingleProfile(false)
                .withSelectionListEnabled(false)
                .withThreeSmallProfileImages(false)
                .withProfileImagesClickable(false)
                .withOnlyMainProfileImageVisible(true)
                .withTextColor(Color.parseColor("black"))
                .build();

        //account_header
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Customer Information").child(id);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getCusName(id);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getCusName(id);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                getCusName(id);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                        return false;
                    }
                })
                .withCloseOnClick(true)
                .withDelayOnDrawerClose(1)
                .build();

        result.closeDrawer();
//nav_drawer components-----------------------------------------------------------------------------
        checkProductGroupDuration();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_purchase:
                    item.setCheckable(true);
                    fragment = new PurchaseFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_group:
                    item.setCheckable(true);
                    fragment = new GroupFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_watchlist:
                    item.setCheckable(true);
                    fragment = new WatchListFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_profile:
                    item.setCheckable(true);
                    fragment = new ProfileFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment) {
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
        searchView.setQueryHint("Enter Product Information");
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
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().popBackStack();
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
                        headerResult.clear();
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
                        .setContentText("A group for your Watchlist item ," + productName + ", was created!")
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
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    final String productID = snapshot.child("pg_pro_ID").getValue().toString();
                    final String endDate = snapshot.child("pg_dateEnd").getValue().toString();
                    final String todayDate = df.format(c.getTime());

                    try {
                        Date dateEnd = df.parse(endDate);
                        Date today = df.parse(todayDate);

                        if (dateEnd.before(today) || dateEnd.equals(today)) {
                            DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID);
                            dbProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    double totalQty = Double.parseDouble(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                                    double minAcceptedOrder = Double.parseDouble(dataSnapshot.child("pro_minOrderAccepted").getValue().toString());
                                    final String productName = dataSnapshot.child("pro_name").getValue().toString();
                                    int minTarget = (int)((totalQty * (minAcceptedOrder / 100)) + 0.5); //Get the min quantity
                                    int maxTarget = (int)(totalQty);

                                    final String pro_minOrderQtySellPrice = dataSnapshot.child("pro_minOrderQtySellPrice").getValue().toString();
                                    final String shippingFee = dataSnapshot.child("pro_shippingCost").getValue().toString();
                                    final String freeShipping;
                                    if (dataSnapshot.hasChild("pro_freeShippingAt")) {
                                        freeShipping = dataSnapshot.child("pro_freeShippingAt").getValue().toString();
                                    } else {
                                        freeShipping = null;
                                    }
                                    calculateCurrentOrderedQuantity(productID, minTarget, maxTarget, todayDate, pro_minOrderQtySellPrice, shippingFee, freeShipping, productName);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void calculateCurrentOrderedQuantity(final String productID, final int minOrderQty, final int maxOrderQty, final String today,
                                                 final String orderedPrice, final String shippingFee, final String freeShipping, final String productName) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                }
                if (counter < minOrderQty) {
                    sendNotification(productID, productName, today, "dismiss");
                    loadFragment(new ProductListingFragment());
                    dismissGroupDetail(productID);
                    dismissGroup(productID);
                    updateProductStatus(productID, "dismissed");
                    String Subject = "A group you are currently in has been dismissed";
                    String Body = "Product group for '" + productName + "' has been dismissed on " + today;
                    emailSeller(productID, Subject, Body);

                } else if (counter > minOrderQty && counter < maxOrderQty) {
                    DatabaseReference dbGroupProduct = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
                    dbGroupProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String customerID = snapshot.child("gd_cus_ID").getValue().toString();
                                String orderedQty = snapshot.child("gd_qty").getValue().toString();
                                checkForCheckout(productID, productName, today, orderedPrice, freeShipping, shippingFee, orderedQty, customerID);
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

    private void checkForCheckout(final String productID, final String productName, final String today, final String orderedPrice,
                                  final String freeShipping, final String shippingFee, final String orderedQty, final String customerID) {
        DatabaseReference dbOrderHistory = FirebaseDatabase.getInstance().getReference("Order History").child(customerID);
        dbOrderHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean duplicatedCheckout = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String oh_pro_ID = snapshot.child("oh_pro_ID").getValue().toString();
                    if (oh_pro_ID.equalsIgnoreCase(productID)) {
                        duplicatedCheckout = true;
                        break;
                    }
                }
                if (!duplicatedCheckout) {
                    String Subject = "Checkout successful for one of your product";
                    String SubjectForCustomer = "A product group you are in has been checkout";
                    String Body = "Product group for " + productName + " has been checkout on " + today;

                    if (freeShipping != null) {
                        float freeShipment = Float.parseFloat(freeShipping);
                        float netPrice = (Float.parseFloat(orderedPrice)) * (Float.parseFloat(orderedQty));

                        if (netPrice >= freeShipment) {
                            String noShippingFee = "0";
                            checkout(productID, customerID, Integer.parseInt(orderedQty), today, orderedPrice, noShippingFee);
                            sendNotification(productID, productName, today, "checkout");
                            emailCustomer(customerID, SubjectForCustomer, Body);

                        } else if (netPrice < freeShipment){
                            checkout(productID, customerID, Integer.parseInt(orderedQty), today, orderedPrice, shippingFee);
                            sendNotification(productID, productName, today, "checkout");
                            emailCustomer(customerID, SubjectForCustomer, Body);
                        }

                    } else {
                        checkout(productID, customerID, Integer.parseInt(orderedQty), today, orderedPrice, shippingFee);
                        sendNotification(productID, productName, today, "checkout");
                        emailCustomer(customerID, SubjectForCustomer, Body);
                    }
                    emailSeller(productID, Subject, Body);
                    updateProductStatus(productID, "sold");
                    dismissGroupDetail(productID);
                    dismissGroup(productID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void dismissGroupDetail(final String productID) {
        DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        dbGroupDetail.removeValue();
    }

    public void dismissGroup(final String productID) {
        DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group").child(productID);
        dbProductGroup.removeValue();
        removeNotification(productID);
        checkWatchlist(productID);
    }

    private void checkWatchlist(final String productID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.hasChild(productID)) {
                        Log.d("12345", snapshot.getKey());
                        String customerID = snapshot.getKey();
                        ProductListingFragment.removeFromWatchList(productID, customerID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void removeProduct(final String productID) {
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID);
        dbProduct.removeValue();
    }

    public void updateProductStatus(final String productID, final String status) {
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID).child("pro_Status");
        dbProduct.setValue(status);
    }

    public void checkout(final String productID, final String customerID, final int orderQty, final String checkoutDate, final String orderedPrice, final String shippingCost) {
        //Updating Order History
        loadFragment(new ProductListingFragment());
        DatabaseReference dbOrderHistory = FirebaseDatabase.getInstance().getReference("Order History").child(customerID);
        String oh_ID = dbOrderHistory.push().getKey();
        orderHistoryClass orderHistoryClass = new orderHistoryClass(oh_ID, productID, customerID, "Processing", orderQty, checkoutDate, orderedPrice, shippingCost);
        dbOrderHistory.child(oh_ID).setValue(orderHistoryClass);

        //Check Duplication
        dbOrderHistory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String check = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("oh_pro_ID").getValue().toString().equalsIgnoreCase(check)) {
                        String oh_ID = snapshot.child("oh_ID").getValue().toString();
                        DatabaseReference dbRemove = FirebaseDatabase.getInstance().getReference("Order History").child(customerID).child(oh_ID);
                        dbRemove.removeValue();
                    } else {
                        check = snapshot.child("oh_pro_ID").getValue().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //Deducting Loyalty Point before adding
        float itemPrice = Float.parseFloat(orderedPrice);
        float shipment = Float.parseFloat(shippingCost);
        Float netPayment = (itemPrice * orderQty) + shipment;
        deductingPaymentWithLoyaltyPoint(customerID, netPayment);

        if (true) {
            //Adding new loyalty after payment
            addLoyaltyPoint(customerID, orderedPrice, orderQty);
        } else {
            blacklistCard(customerID);
        }
    }

    private void addLoyaltyPoint(final String customerID, final String orderedPrice, final int orderQty) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Customer Information").child(customerID).child("cus_loyaltyPoint");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference updateLoyaltyPoint = FirebaseDatabase.getInstance().getReference("Customer Information").child(customerID).child("cus_loyaltyPoint");
                int loyaltyPoint = Integer.parseInt(dataSnapshot.getValue().toString());
                double qty = orderQty;
                double amountPaid = Double.parseDouble(orderedPrice) * qty;
                int calculateLoyaltyPointEarn = (int)amountPaid;
                loyaltyPoint = loyaltyPoint + calculateLoyaltyPointEarn;
                updateLoyaltyPoint.setValue(loyaltyPoint);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deductLoyaltyPoint(final String customerID,final int amount) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Customer Information").child(customerID).child("cus_loyaltyPoint");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference updateLoyaltyPoint = FirebaseDatabase.getInstance().getReference("Customer Information").child(customerID).child("cus_loyaltyPoint");
                int loyaltyPoint = Integer.parseInt(dataSnapshot.getValue().toString());
                loyaltyPoint = loyaltyPoint - amount;
                updateLoyaltyPoint.setValue(loyaltyPoint);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(final String productID, final String productName, final String today, final String condition){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String customerID = snapshot.child("gd_cus_ID").getValue().toString();

                    String noti_Title = "";
                    String noti_Description = "";
                    if (condition.equalsIgnoreCase("dismiss")) {
                        noti_Title = "A group you are currently in has been dismissed";
                        noti_Description = "Product group for '" + productName + "' has been dismissed on " + today + " due to insufficient order";
                    } else if (condition.equalsIgnoreCase("checkout")) {
                        noti_Title = "Checkout successful for one of your groups";
                        noti_Description = "Checkout was successfully processed for '" + productName + "' on " + today;
                    }

                    ProductListingFragment.sendNotification(productID, noti_Title, noti_Description, today, customerID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeNotification(final String productID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Notification");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshotAgain : snapshot.getChildren()) {
                        if (snapshotAgain.child("noti_prodID").getValue().toString().equalsIgnoreCase(productID)) {
                            if (snapshotAgain.child("noti_Title").getValue().toString().equalsIgnoreCase("A group has been created for the item in your Watchlist")) {
                                String customerID = snapshot.getKey();
                                DatabaseReference dbRemove = FirebaseDatabase.getInstance().getReference("Notification").child(customerID).child(productID);
                                dbRemove.removeValue();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void emailSeller(final String ProductID, final String Subject, final String Body) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product").child(ProductID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String sellerID = dataSnapshot.child("pro_s_ID").getValue().toString();
                DatabaseReference dbSellerInfo = FirebaseDatabase.getInstance().getReference("User").child(sellerID);
                dbSellerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sellerEmail = dataSnapshot.child("email").getValue().toString();
                        resetPWActivity.sendMailRelatedToProduct(sellerEmail, Subject, Body);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void emailCustomer(final String customerID, final String Subject, final String Body) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("User").child(customerID).child("email");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.getValue().toString();
                resetPWActivity.sendMailRelatedToProduct(email, Subject, Body);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void blacklistCard(String customerID) {
        DatabaseReference dbCCInfo = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(customerID).child("cc_Num");
        dbCCInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String cardNum = dataSnapshot.getValue().toString();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Blacklisted Card");
                String bcc_ID = db.push().getKey();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String todayDate = df.format(c.getTime());

                blacklistedCreditCardClass blacklistedCreditCardClass = new blacklistedCreditCardClass(bcc_ID, todayDate, cardNum);
                db.child(cardNum).setValue(blacklistedCreditCardClass);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deductingPaymentWithLoyaltyPoint(final String customerID, final Float netPayment) {
        DatabaseReference dbCustomerInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(customerID).child("cus_loyaltyPoint");
        dbCustomerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int currentLoyaltyPoint = Integer.parseInt(dataSnapshot.getValue().toString());
                double deduction = netPayment * 0.1;
                int getWholeNumber = (int)deduction;
                if (currentLoyaltyPoint >= getWholeNumber) {
                    //Deduct Loyalty Point
                    deductLoyaltyPoint(customerID, getWholeNumber);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
