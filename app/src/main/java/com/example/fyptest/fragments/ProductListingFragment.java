package com.example.fyptest.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.Adapters.CustomAdapter;
import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.database.groupDetailClass;
import com.example.fyptest.database.notificationClass;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ProductListingFragment extends Fragment {
    RecyclerView mRecyclerView;
    CustomAdapter mAdapter;
    DatabaseReference databaseProduct;
    List<productClass> prodList;
    Context mContext;
    int qtyChosenVal;
    TextView qtyText;
    boolean[] abc;
    SharedPreferences prefs;
    String userIdentity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_productlisting, container, false);
        this.mRecyclerView = groupView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.mContext = getContext();

        this.prefs = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = prefs.getString("userID", "UNKNOWN");
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        abc = new boolean[1];
        displayProduct();
    }

    public void displayProduct () {
        prodList = new ArrayList<>();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prodList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                        if (productSnapshot.child("pro_Status").getValue().toString().equals("approved")) {
                            productClass product = productSnapshot.getValue(productClass.class);
                            prodList.add(product);
                        }
                    }
                    mAdapter = new CustomAdapter(getActivity(), prodList);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void ShowDialog(final Context context, final String prodID, final String prodName, final Button button, final int option, final String gdCusID) {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);
        LinearLayout linear = new LinearLayout(context);
        final int seekMin = 1;

        linear.setOrientation(LinearLayout.VERTICAL);
        qtyText = new TextView(context);
        qtyText.setPadding(10, 10, 10, 10);
        qtyText.setGravity(Gravity.CENTER_HORIZONTAL);

        final SeekBar seek = new SeekBar(context);

        DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
        dbGroupDetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if (dataSnapshot.hasChildren()) {
                    int counter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                    }
                    final int finalCount = counter;
                    DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
                    dbProduct.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int target = Integer.parseInt(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                            int condition = target - finalCount;
                            if (condition >= 10) {
                                seek.setMax(10);
                                qtyText.setText(seekMin + "/" + seek.getMax());
                            } else if (condition < 10) {
                                seek.setMax(condition);
                                qtyText.setText(seekMin + "/" + seek.getMax());
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                //}
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        linear.addView(seek);
        linear.addView(qtyText);

        popDialog.setView(linear);

        popDialog.setTitle("Please Select Quantity for " + prodName);


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qtyChosenVal = seekMin + progress;
                if (qtyChosenVal > seekBar.getMax()) {
                    qtyChosenVal = seekBar.getMax();
                }
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                qtyText.setText(qtyChosenVal + "/" + seekBar.getMax());
            }
        });

        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeFromWatchList(prodID, gdCusID);
                setButtonToViewGroup(button, context);
                if (option == 1) {
                    insertCustGroupDetails(prodID, gdCusID);
                    removeNotification(gdCusID, prodID);
                    checkForCheckout(prodID);
                } else if (option == 2) {
                    insertProductGroup(prodID);
                    insertCustGroupDetails(prodID, gdCusID);
                    checkForCheckout(prodID);
                }
            }
        });

        popDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = popDialog.create();
        alertdialog.show();
    }

    public static String addDay(String oldDate, String duration) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Calendar c = Calendar.getInstance();
        int numberofDays = Integer.parseInt(duration);
        try {
            c.setTime(dateFormat.parse(oldDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DAY_OF_YEAR,numberofDays);
        dateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date newDate=new Date(c.getTimeInMillis());
        String resultDate=dateFormat.format(newDate);
        return resultDate;
    }

    private void removeFromWatchList(String prodID, String gdCusID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List").child(gdCusID).child(prodID);
        db.removeValue();
    }

    private void setButtonToViewGroup(Button button, final Context context) {
        button.setText("View Group");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToGroupFragment(context);
            }
        });
    }

    public void swapToGroupFragment (Context mContext) {
        Activity activity = (FragmentActivity) mContext;
        GroupFragment newGroupFragment = new GroupFragment();
        Log.d("activity ", "value: " + activity);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void insertProductGroup (final String prodID) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        final String string_pgDateCreated = df.format(c.getTime());

        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pro_durationForGroupPurchase = dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString();
                final String productName = dataSnapshot.child("pro_name").getValue().toString();
                String dateEnd = addDay(string_pgDateCreated, pro_durationForGroupPurchase);
                databaseProduct = FirebaseDatabase.getInstance().getReference("Product Group");
                productGroupClass productGroup = new productGroupClass(prodID, dateEnd, string_pgDateCreated);
                databaseProduct.child(prodID).setValue(productGroup);

                DatabaseReference dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
                dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final String customerID = snapshot.getKey();
                            for (DataSnapshot snapshotAgain : snapshot.getChildren()) {
                                if (snapshotAgain.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                                    String noti_Title = "A group has been created for the item in your Watchlist";
                                    String noti_Description = "Click here to view information about " + productName;
                                    sendNotification(prodID, noti_Title, noti_Description, string_pgDateCreated, customerID);
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void sendNotification(final String prodID, final String noti_Title, final String noti_Description, final String todayDate, final String customerID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Notification");
        String noti_ID = db.push().getKey();
        notificationClass notificationClass = new notificationClass(noti_ID, noti_Title, noti_Description, todayDate, prodID);
        db.child(customerID).child(prodID).setValue(notificationClass);
    }

    public static void removeNotification(final String customerID, final String productID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Notification").child(customerID).child(productID);
        db.removeValue();
    }

    private void insertCustGroupDetails (final String prodGroupId, final String gdCusID) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String gdJoinDate = df.format(c.getTime());
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail");
        String pg_ID = db.push().getKey();
        groupDetailClass groupDetail =  new groupDetailClass(pg_ID, gdJoinDate, qtyChosenVal, prodGroupId, gdCusID);
        db.child(prodGroupId).child(pg_ID).setValue(groupDetail);
    }

    private void checkForCheckout(final String productID) {
        final DatabaseReference checkTotalQty = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        checkTotalQty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                }
                final int finalCount = counter;
                DatabaseReference getTargetQty = FirebaseDatabase.getInstance().getReference("Product").child(productID);
                getTargetQty.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int targetQty = Integer.parseInt(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                        final String productName = dataSnapshot.child("pro_name").getValue().toString();
                        final String pro_maxOrderQtySellPrice = dataSnapshot.child("pro_maxOrderQtySellPrice").getValue().toString();
                        final String shippingFee = dataSnapshot.child("pro_shippingCost").getValue().toString();
                        final String freeShipping;
                        if (dataSnapshot.hasChild("pro_freeShippingAt")) {
                            freeShipping = dataSnapshot.child("pro_freeShippingAt").getValue().toString();
                        } else {
                            freeShipping = null;
                        }

                        if (finalCount == targetQty) {
                            DatabaseReference groupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
                            groupDetail.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    MainActivity ma = new MainActivity();

                                    Calendar c = Calendar.getInstance();
                                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                    String todayDate = df.format(c.getTime());

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String customerID = snapshot.child("gd_cus_ID").getValue().toString();
                                        String qtyOrdered = snapshot.child("gd_qty").getValue().toString();

                                        if (freeShipping != null) {
                                            float freeShipment = Float.parseFloat(freeShipping);
                                            float netPrice = (Float.parseFloat(pro_maxOrderQtySellPrice)) * (Float.parseFloat(qtyOrdered));

                                            if (netPrice >= freeShipment) {
                                                String noShippingFee = "0";
                                                ma.checkout(productID, customerID, Integer.parseInt(qtyOrdered), todayDate, pro_maxOrderQtySellPrice, noShippingFee);
                                                ma.sendNotification(productID, productName, todayDate, "checkout");
                                                ma.dismissGroupDetail(productID);

                                            } else {
                                                ma.checkout(productID, customerID, Integer.parseInt(qtyOrdered), todayDate, pro_maxOrderQtySellPrice, shippingFee);
                                                ma.sendNotification(productID, productName, todayDate, "checkout");
                                                ma.dismissGroupDetail(productID);

                                            }
                                        } else {
                                            ma.checkout(productID, customerID, Integer.parseInt(qtyOrdered), todayDate, pro_maxOrderQtySellPrice, shippingFee);
                                            ma.sendNotification(productID, productName, todayDate, "checkout");
                                            ma.dismissGroupDetail(productID);

                                        }
                                    }
                                    String Subject = "A group for your product has been checkout";
                                    String Body = "Product group for " + productName + " has been checkout on " + todayDate;

                                    ma.dismissGroup(productID);
                                    ma.emailSeller(productID, Subject, Body);
                                    //ma.removeProduct(productID);
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkBlacklistedCard(final Button button, final String userID) {
        DatabaseReference dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(userID);
        dbCC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String ccID = dataSnapshot.child("cc_ID").getValue().toString();

                final DatabaseReference dbBL = FirebaseDatabase.getInstance().getReference("Blacklisted Card");
                dbBL.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(ccID)) {
                            button.setVisibility(View.INVISIBLE);

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail");
                            db.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        for (DataSnapshot snapshotAgain : snapshot.getChildren()) {
                                            Log.d("12345", Boolean.toString(snapshotAgain.hasChild(userID)));
                                            if (snapshotAgain.hasChild(userID)) {
                                                String productID = snapshot.child("gd_pg_pro_ID").getValue().toString();
                                                String groupID = snapshot.child("gd_ID").getValue().toString();
                                                DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID).child(groupID);
                                                dbGroupDetail.removeValue();
                                                break;
                                            }
                                        }
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

