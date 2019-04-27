package com.example.fyptest.fragments;

import android.app.Activity;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class ProductView extends Fragment {
    DatabaseReference databaseProduct, dbGroupDetails, dbGroup;

    TextView pvName, categoryTV, descTV, durationTV, originalTV, discTV_1, discTV_2, targetqtyTV,
            purchaseqtyTV, shippingTV, minDiscPercent;

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
        databaseProduct.addValueEventListener(new ValueEventListener() {
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
                durationTV.setText(dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString());
                originalTV.setText(dataSnapshot.child("pro_retailPrice").getValue().toString());
                discTV_1.setText(dataSnapshot.child("pro_maxOrderQtySellPrice").getValue().toString());
                discTV_2.setText(dataSnapshot.child("pro_minOrderQtySellPrice").getValue().toString());
                shippingTV.setText(dataSnapshot.child("pro_shippingCost").getValue().toString());
                minDiscPercent.setText("*if " + dataSnapshot.child("pro_minOrderDiscount").getValue().toString() + "% target quantity met");

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
                                    groupBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
                                }
                            }
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

//        dbGroupDetails = FirebaseDatabase.getInstance().getReference("Group Detail");
//        dbGroup = FirebaseDatabase.getInstance().getReference("Product Group");
//        databaseProduct.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
//                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
//                    if (productSnapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
//                        Picasso.get()
//                                .load(productSnapshot.child("pro_mImageUrl").getValue().toString())
//                                .fit()
//                                .centerCrop()
//                                .into(image);
//                        pvName.setText(productSnapshot.child("pro_name").getValue().toString());
//                        categoryTV.setText(productSnapshot.child("pro_productType").getValue().toString());
//                        descTV.setText(productSnapshot.child("pro_description").getValue().toString());
//                        durationTV.setText(productSnapshot.child("pro_durationForGroupPurchase").getValue().toString());
//                        originalTV.setText(productSnapshot.child("pro_retailPrice").getValue().toString());
//                        discTV_1.setText(productSnapshot.child("pro_maxOrderQtySellPrice").getValue().toString());
//                        discTV_2.setText(productSnapshot.child("pro_minOrderQtySellPrice").getValue().toString());
//                        targetqtyTV.setText(productSnapshot.child("pro_targetQuantity").getValue().toString());
//                        shippingTV.setText(productSnapshot.child("pro_shippingCost").getValue().toString());
//                        minDiscPercent.setText("*if " + productSnapshot.child("pro_minOrderDiscount").getValue().toString() + "% target quantity met");
//                        dbGroupDetails.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
//                                if (dataSnapshot2.hasChild(prodID)) {
//                                    for (DataSnapshot gdSnapshot: dataSnapshot2.getChildren()){
//                                        if (gdSnapshot.exists()) {
//                                            DatabaseReference db2 = dbGroupDetails.child(prodID);
//                                            db2.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
//                                                    for (DataSnapshot gdSnapshot3: dataSnapshot3.getChildren()) {
//                                                        if (gdSnapshot3.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
//                                                            purchaseqtyTV.setText(gdSnapshot3.child("gd_qty").getValue().toString());
//                                                        } else {
//                                                            purchaseqtyTV.setText("-");
//                                                        }
//                                                    }
//                                                }
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                        } else {
//                                            purchaseqtyTV.setText("-");
//                                        }
//                                    }
//                                } else {
//                                    purchaseqtyTV.setText("-");
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        dbGroup.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.hasChild(prodID)) {
//                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
//                    db.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                if (userID.equalsIgnoreCase(snapshot.child("gd_cus_ID").getValue().toString())) {
//                                    groupBtn.setText("Leave Group");
//                                    removeFromWatchList(prodID, userID);
//                                    watchBtn.setVisibility(View.INVISIBLE);
//                                    groupBtn.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            pl.swapToGroupFragment(getContext());
//                                        }
//                                    });
//                                    break;
//                                } else {
//                                    DatabaseReference dbPro = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
//                                    dbPro.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            String productName = dataSnapshot.child("pro_name").getValue().toString();
//                                            setToCreateOrJoinGroup(groupBtn, prodID, productName , "Join Group", 1, userID, getContext());
//                                            changeWatchButton(watchBtn, userID, prodID, getContext());
//                                        }
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                } else if (!dataSnapshot.hasChild(prodID)) {
//                    DatabaseReference dbPro = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
//                    dbPro.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            String productName = dataSnapshot.child("pro_name").getValue().toString();
//                            setToCreateOrJoinGroup(groupBtn, prodID, productName , "Create Group", 2, userID, getContext());
//                            changeWatchButton(watchBtn, userID, prodID, getContext());
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
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
        button.setText(" REMOVE FROM WATCH LIST ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromWatchList(prodID, gdCusID);
            }
        });
    }

    private void swapToWatchListGragment (Context mContext) {
        Activity activity = (FragmentActivity) mContext;
        WatchListFragment newGroupFragment = new WatchListFragment();
        Log.d("activity ", "value: " + activity);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    private interface FirebaseCallback {
        void onCallBack(List<productClass> list);
    }

    private void readData (final FirebaseCallback firebaseCallback, String prodID) {
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productClass productClass = dataSnapshot.getValue(productClass.class);
                productList.add(productClass);
                firebaseCallback.onCallBack(productList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
