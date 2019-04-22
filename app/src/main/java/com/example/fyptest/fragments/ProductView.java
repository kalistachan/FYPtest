package com.example.fyptest.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductView extends Fragment {
    DatabaseReference databaseProduct;
    DatabaseReference dbGroupDetails;
    TextView pvName, categoryTV, descTV, durationTV, originalTV, discTV_1, discTV_2, targetqtyTV,
            purchaseqtyTV, shippingTV, minDiscPercent;
    ImageView image;


    public ProductView() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View groupView = inflater.inflate(R.layout.fragment_product_view, container, false);
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
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        dbGroupDetails = FirebaseDatabase.getInstance().getReference("Group Detail");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    if (productSnapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                        Picasso.get()
                                .load(productSnapshot.child("pro_mImageUrl").getValue().toString())
                                .fit()
                                .centerCrop()
                                .into(image);
                        pvName.setText(productSnapshot.child("pro_name").getValue().toString());
                        categoryTV.setText(productSnapshot.child("pro_productType").getValue().toString());
                        descTV.setText(productSnapshot.child("pro_description").getValue().toString());
                        durationTV.setText(productSnapshot.child("pro_durationForGroupPurchase").getValue().toString());
                        originalTV.setText(productSnapshot.child("pro_retailPrice").getValue().toString());
                        discTV_1.setText(productSnapshot.child("pro_maxOrderQtySellPrice").getValue().toString());
                        discTV_2.setText(productSnapshot.child("pro_minOrderQtySellPrice").getValue().toString());
                        targetqtyTV.setText(productSnapshot.child("pro_targetQuantity").getValue().toString());
                        shippingTV.setText(productSnapshot.child("pro_shippingCost").getValue().toString());
                        minDiscPercent.setText("*if " + productSnapshot.child("pro_minOrderDiscount").getValue().toString() + "% target quantity met");
                        dbGroupDetails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                if (dataSnapshot2.hasChild(prodID)) {
                                    for (DataSnapshot gdSnapshot: dataSnapshot2.getChildren()){
                                        if (gdSnapshot.exists()) {
                                            DatabaseReference db2 = dbGroupDetails.child(prodID);
                                            db2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                                    for (DataSnapshot gdSnapshot3: dataSnapshot3.getChildren()) {
                                                        if (gdSnapshot3.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                                                            purchaseqtyTV.setText(gdSnapshot3.child("gd_qty").getValue().toString());
                                                        } else {
                                                            purchaseqtyTV.setText("-");
                                                        }
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            purchaseqtyTV.setText("-");
                                        }
                                    }
                                } else {
                                    purchaseqtyTV.setText("-");
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
