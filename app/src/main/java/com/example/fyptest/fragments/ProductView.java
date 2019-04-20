package com.example.fyptest.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductView extends Fragment {
    DatabaseReference databaseProduct;
    TextView pvName;
    TextView categoryTV;
    TextView descTV;
    TextView durationTV;
    TextView originalTV;
    TextView discTV_1;
    TextView discTV_2;
    TextView targetqtyTV;
    TextView purchaseqtyTV;
    TextView shippingTV;
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
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        String desired_string = arguments.getString("ProdID");
        recyclerViewListClicked(desired_string);
    }


    public void recyclerViewListClicked(final String prodID){
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    if (productSnapshot.child("pro_ID").getValue().toString().equals(prodID)) {
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
                        // purchaseqty need to get from group detail table
                       // purchaseqtyTV.setText(productSnapshot.child("pro_productType").getValue().toString());
                        shippingTV.setText(productSnapshot.child("pro_shippingCost").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState, int position) {
        super.onViewCreated(view, savedInstanceState);
        displayProductView(position);
    }

    private void displayProductView(int position) {

    }
    */
}
