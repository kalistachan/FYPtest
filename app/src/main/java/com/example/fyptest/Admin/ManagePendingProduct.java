package com.example.fyptest.Admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.fyptest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ManagePendingProduct extends Fragment {
    ImageView imgView;
    EditText editProductName, editProductPrice, duration, editTextProdDesc, maxDis,
            minTar, minDis, editTextShipCost, editTextFreeShipCondition, editTextTQ;
    TextView textViewMaxPrice, textViewMinPrice;
    Spinner productType;
    CheckBox checkBoxFreeShipment;
    String prodID;
    Bundle arguments;
    Button buttonAddProduct, buttonCancelProduct;

    public ManagePendingProduct() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_pending_product, container, false);
        arguments = getArguments();
        prodID = arguments.getString("ProdID");

        this.imgView = (ImageView) view.findViewById(R.id.imgView);
        this.productType = (Spinner) view.findViewById(R.id.productType);
        this.checkBoxFreeShipment = (CheckBox) view.findViewById(R.id.checkBoxFreeShipment);

        this.editProductName = (EditText) view.findViewById(R.id.editProductName);
        this.editProductPrice = (EditText) view.findViewById(R.id.editProductPrice);
        this.duration = (EditText) view.findViewById(R.id.duration);
        this.editTextProdDesc = (EditText) view.findViewById(R.id.editTextProdDesc);
        this.maxDis = (EditText) view.findViewById(R.id.maxDis);
        this.minTar = (EditText) view.findViewById(R.id.minTar);
        this.minDis = (EditText) view.findViewById(R.id.minDis);
        this.editTextShipCost = (EditText) view.findViewById(R.id.editTextShipCost);
        this.editTextFreeShipCondition = (EditText) view.findViewById(R.id.editTextFreeShipCondition);
        this.editTextTQ = (EditText) view.findViewById(R.id.editTextTQ);

        this.textViewMaxPrice = (TextView) view.findViewById(R.id.textViewMaxPrice);
        this.textViewMinPrice = (TextView) view.findViewById(R.id.textViewMinPrice);

        this.buttonAddProduct = (Button) view.findViewById(R.id.buttonAddProduct);
        this.buttonCancelProduct = (Button) view.findViewById(R.id.buttonCancelProduct);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillAddProdContent();

        maxDis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text1 = s.toString();
                String text2 = editProductPrice.getText().toString();
                if (!TextUtils.isEmpty(text1)) {
                    if (!TextUtils.isEmpty(text2)) {
                        float retailPrice = Float.parseFloat(text2);
                        float value = 100;
                        float maxDisc = Float.parseFloat(text1) / value;
                        float maxSellPrice = retailPrice - (retailPrice * maxDisc);
                        String floatToString = String.format("%.2f", maxSellPrice);
                        textViewMaxPrice.setText("$" + (floatToString));
                    } else {
                        editProductPrice.setHint("$0.00");
                        textViewMaxPrice.setText("$0.00");
                    }
                } else {
                    textViewMaxPrice.setText("$0.00");
                    maxDis.setHint("100%");
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {}
        });
        minDis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text1 = s.toString();
                String text2 = editProductPrice.getText().toString();
                if (!TextUtils.isEmpty(text1)) {
                    if (!TextUtils.isEmpty(text2)) {
                        float retailPrice = Float.parseFloat(text2);
                        float value = 100;
                        float minDisc = Float.parseFloat(text1) / value;
                        float maxSellPrice = retailPrice - retailPrice * minDisc;
                        String floatToString = String.format("%.2f", maxSellPrice);
                        textViewMinPrice.setText("$" + (floatToString));
                    } else {
                        editProductPrice.setHint("$0.00");
                        textViewMinPrice.setText("$0.00");
                    }
                } else {
                    textViewMinPrice.setText("$0.00");
                    minDis.setHint("100%");
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {}
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approvePendingProduct(prodID);
                navigateAdminMain();
            }
        });

        buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectPendingProduct(prodID);
                arguments.clear();
                navigateAdminMain();
            }
        });
    }

    private void setSpinText(Spinner spin, String text) {
        for (int i = 0 ; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }

    private void navigateAdminMain() {
        Fragment newFragment = new AdminMainFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void fillAddProdContent() {
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        dbProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pro_Status").getValue().toString().equalsIgnoreCase("pending")) {
                    Picasso.get()
                            .load(dataSnapshot.child("pro_mImageUrl").getValue().toString())
                            .into(imgView);
                    editProductName.setText(dataSnapshot.child("pro_name").getValue().toString());
                    setSpinText(productType, dataSnapshot.child("pro_productType").getValue().toString());
                    duration.setText(dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString());
                    editTextProdDesc.setText(dataSnapshot.child("pro_description").getValue().toString());
                    editTextTQ.setText(dataSnapshot.child("pro_targetQuantity").getValue().toString());
                    editProductPrice.setText(dataSnapshot.child("pro_retailPrice").getValue().toString());
                    maxDis.setText(dataSnapshot.child("pro_maxOrderDiscount").getValue().toString());
                    minTar.setText(dataSnapshot.child("pro_minOrderAccepted").getValue().toString());
                    minDis.setText(dataSnapshot.child("pro_minOrderDiscount").getValue().toString());
                    String shipCost = "$" + dataSnapshot.child("pro_shippingCost").getValue().toString();
                    editTextShipCost.setText(shipCost);

                    productType.setEnabled(false);

                    if (dataSnapshot.hasChild("pro_freeShippingAt")) {
                        String freeShipping = "$" + dataSnapshot.child("pro_freeShippingAt").getValue().toString();
                        checkBoxFreeShipment.setChecked(true);
                        editTextFreeShipCondition.setEnabled(true);
                        editTextFreeShipCondition.setText(freeShipping);
                    } else if (!dataSnapshot.hasChild("pro_freeShippingAt")) {
                        checkBoxFreeShipment.setChecked(false);
                        editTextFreeShipCondition.setEnabled(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void rejectPendingProduct(String prodID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        db.removeValue();
    }

    private void approvePendingProduct(String prodID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product").child(prodID).child("pro_Status");
        db.setValue("approved");
    }
}
