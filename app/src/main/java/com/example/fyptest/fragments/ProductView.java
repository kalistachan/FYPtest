package com.example.fyptest.fragments;

import android.app.Activity;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.watchlistClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProductView extends Fragment {
    DatabaseReference databaseProduct, dbGroupDetails;

    TextView pvName, categoryTV, descTV, durationTV, originalTV, discTV_1, discTV_2, targetqtyTV,
            purchaseqtyTV, shippingTV, minDiscPercent, freeShipping;

    ImageView image;

    Button groupBtn, watchBtn;

    Context context;

    ProductListingFragment pl;

    List<productClass> productList;

    public ProductView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View groupView = inflater.inflate(R.layout.fragment_product_view, container, false);
        context = getContext();
        productList = new ArrayList<>();

        pvName = groupView.findViewById(R.id.pv_name);
        categoryTV = groupView.findViewById(R.id.categoryTV);
        image = groupView.findViewById(R.id.image_view_upload2);
        descTV = groupView.findViewById(R.id.productdescTV);
        durationTV = groupView.findViewById(R.id.durationTV);
        originalTV = groupView.findViewById(R.id.originalTV);
        discTV_1 = groupView.findViewById(R.id.dc1TV);
        discTV_2 = groupView.findViewById(R.id.dc2TV);
        targetqtyTV = groupView.findViewById(R.id.tqTV);
        purchaseqtyTV = groupView.findViewById(R.id.pqQty);
        shippingTV = groupView.findViewById(R.id.sfTV);
        minDiscPercent = groupView.findViewById(R.id.minDiscPercent);
        groupBtn = groupView.findViewById(R.id.button2);
        watchBtn = groupView.findViewById(R.id.button3);
        freeShipping = groupView.findViewById(R.id.fsTV);

        pl = new ProductListingFragment();
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        String prodID = arguments.getString("ProdID");
        String userID = arguments.getString("CusID");
        recyclerViewListClicked(prodID, userID);
    }

    public void recyclerViewListClicked(final String prodID, final String userID){
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        databaseProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get()
                        .load(dataSnapshot.child("pro_mImageUrl").getValue().toString())
                        .fit()
                        .centerCrop()
                        .into(image);
                final String targetQty = dataSnapshot.child("pro_targetQuantity").getValue().toString();
                final String productName = dataSnapshot.child("pro_name").getValue().toString();
                pvName.setText(productName);
                categoryTV.setText(dataSnapshot.child("pro_productType").getValue().toString());
                descTV.setText(dataSnapshot.child("pro_description").getValue().toString());
                durationTV.setText(dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString() + " Days");
                originalTV.setText("$" + dataSnapshot.child("pro_retailPrice").getValue().toString());
                discTV_1.setText(dataSnapshot.child("pro_maxOrderQtySellPrice").getValue().toString());
                discTV_2.setText(dataSnapshot.child("pro_minOrderQtySellPrice").getValue().toString());
                shippingTV.setText("$" + dataSnapshot.child("pro_shippingCost").getValue().toString());
                minDiscPercent.setText("*if " + dataSnapshot.child("pro_minOrderDiscount").getValue().toString() + "% target quantity met");
                if (dataSnapshot.hasChild("pro_freeShippingAt")) {
                    freeShipping.setText("$" + dataSnapshot.child("pro_freeShippingAt").getValue().toString());
                } else if (!dataSnapshot.hasChild("pro_freeShippingAt")) {
                    freeShipping.setText("Not Applicable");
                }

                dbGroupDetails = FirebaseDatabase.getInstance().getReference("Group Detail");
                dbGroupDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(prodID)) {
                            int count = 0;
                            setToCreateOrJoinGroup(groupBtn, prodID, productName, "JOIN GROUP", 1, userID, context);
                            changeWatchButton(watchBtn, userID, prodID, context);
                            for (DataSnapshot snapshot : dataSnapshot.child(prodID).getChildren()) {
                                count = count + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                                    purchaseqtyTV.setText(snapshot.child("gd_qty").getValue().toString());
                                    watchBtn.setVisibility(View.GONE);
                                    groupBtn.setText("LEAVE GROUP");
                                    checkConditionForLeavingGroup(groupBtn, prodID);
                                    groupBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            leaveGroup(prodID, userID, context);
                                            watchBtn.setVisibility(View.VISIBLE);
                                            setButtonToAddWatchList(prodID, watchBtn, userID, context);
                                        }
                                    });
                                }
                            }
                            double a = count * 0.9;
                            int i = (int)(a + 0.5);
                            String sCount = Integer.toString(count);
                            String construct = sCount + " / " + targetQty;
                            targetqtyTV.setText(construct);
                        } else {
                            purchaseqtyTV.setText("-");
                            targetqtyTV.setText("0 / " + targetQty);
                            setToCreateOrJoinGroup(groupBtn, prodID, productName, "CREATE GROUP", 2, userID, context);
                            changeWatchButton(watchBtn, userID, prodID, context);
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

    public void checkConditionForLeavingGroup(final Button button, final String productID) {
        final DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group").child(productID);
        final DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(productID);

        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float targetQty = Float.parseFloat(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                final String pro_durationForGroupPurchase = dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString();
                final int leavingCondition = (int)((targetQty * 0.9) + 0.5);
                Log.d("Condition Check", "Leaving Condition : " + Integer.toString(leavingCondition));
                dbGroupDetail.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                        }
                        if (counter >= leavingCondition) {
                            button.setEnabled(false);
                        } else if (counter < leavingCondition){
                            button.setEnabled(true);
                            dbProductGroup.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        String dateCreated = dataSnapshot.child("pg_dateCreated").getValue().toString();
                                        String condition = Integer.toString((int)((Integer.parseInt(pro_durationForGroupPurchase)/2) + 0.5));
                                        Log.d("Condition Check", "Date duration 50% : " + condition);

                                        String conditionDate = ProductListingFragment.addDay(dateCreated, condition);

                                        Calendar c = Calendar.getInstance();
                                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                                        String todayDate = df.format(c.getTime());

                                        try {
                                            Date dateNow = df.parse(todayDate);
                                            Date dateMarker = df.parse(conditionDate);
                                            if (dateNow.after(dateMarker)) {
                                                button.setEnabled(false);
                                            } else if (dateNow.before(dateMarker)){
                                                button.setEnabled(true);
                                            }
                                        } catch (ParseException ex) {
                                            Log.v("Exception", ex.getLocalizedMessage());
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

    private void leaveGroup(final String prodID, final String userIdentity, final Context context) {
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 1) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                            String groupDetailID = snapshot.child("gd_ID").getValue().toString();
                            db.child(groupDetailID).removeValue();
                            DatabaseReference dbAgain = FirebaseDatabase.getInstance().getReference("Product Group").child(prodID);
                            dbAgain.removeValue();
                            break;
                        }
                    }
                } else if (dataSnapshot.getChildrenCount() > 1) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                            String groupDetailID = snapshot.child("gd_ID").getValue().toString();
                            db.child(groupDetailID).removeValue();
                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Debug: onCancelled (dbProduct)", databaseError.getMessage());
            }
        });
    }

    private void removeFromWatchList(String prodID, String gdCusID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List").child(gdCusID).child(prodID);
        db.removeValue();
    }

    private void changeWatchButton(final Button button, final String userID, final String prodID, final Context context) {
        DatabaseReference dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
        dbWatchList.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    if (dataSnapshot.child(userID).hasChild(prodID)) {
                        if (dataSnapshot.child(userID).child(prodID).child("wl_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                            setButtonToRemoveWatchList(button, prodID, userID);
                        } else if (!dataSnapshot.child(userID).child(prodID).child("wl_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                            setButtonToAddWatchList(prodID, button, userID, context);
                        }
                    } else if (!dataSnapshot.child(userID).hasChild(prodID)) {
                        setButtonToAddWatchList(prodID, button, userID, context);
                    }
                } else if (!dataSnapshot.hasChild(userID)) {
                    setButtonToAddWatchList(prodID, button, userID, context);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void setButtonToAddWatchList(final String prodID, final Button button, final String gdCusID, final Context context) {
        button.setText(" Add To Watch List ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWatchList(prodID, button, gdCusID, context);
            }
        });
    }

    private void setButtonToRemoveWatchList(Button button, final String prodID, final String gdCusID) {
        button.setText(" Remove From Watch List");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromWatchList(prodID, gdCusID);
            }
        });
    }

    private void addToWatchList(String prodID, Button button, String gdCusID, Context context) {
        DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("Watch List").child(gdCusID);
        String wl_ID = newDB.push().getKey();
        watchlistClass watchlistClass = new watchlistClass(wl_ID, gdCusID, prodID);
        newDB.child(prodID).setValue(watchlistClass);
    }

    private void setToCreateOrJoinGroup(final Button button, final String prodID, final String prodName, String btnName, final int option, final String gdCusID, final Context context) {
        button.setText(btnName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pl.ShowDialog(context, prodID, prodName, button, option, gdCusID);
            }
        });
    }
}
