package com.example.fyptest.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PurchaseFragment extends Fragment {
    EditText editProductName;
    EditText editProductPrice;
    DatabaseReference databaseProduct;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        editProductName = (EditText) getView().findViewById(R.id.editProductName);
        editProductPrice = (EditText) getView().findViewById(R.id.editProductPrice);
        Button buttonAddProduct = (Button) getView().findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addProduct(editProductName,editProductPrice);
            }
        });
    }

    public void addProduct (EditText prodName, EditText prodPrice) {
        databaseProduct = FirebaseDatabase.getInstance().getReference("product");

        String prodNameText = prodName.getText().toString().trim();
        prodName.setText("");

        String prodPriceText = prodPrice.getText().toString().trim();
        prodPrice.setText("");

        if (!TextUtils.isEmpty(prodNameText)) {
            String id = databaseProduct.push().getKey();

            Product product = new Product(id, prodNameText, prodPriceText);
            Log.d("prodName", "test="+prodNameText);
            databaseProduct.child(id).setValue(product);

              Toast.makeText(getActivity().getApplicationContext(), "Product Added", Toast.LENGTH_LONG).show();
        } else {
             Toast.makeText(getActivity().getApplicationContext(),"You should enter a name", Toast.LENGTH_LONG).show();
        }
    }



}
