package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fyptest.Adapters.purchasesAdapter;
import com.example.fyptest.Adapters.watchListAdapter;
import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PurchaseFragment extends Fragment {
    //Retrieving User Identity
    SharedPreferences pref;
    String userIdentity;
    Context context;

    //Retriving RecycleView
    RecyclerView recycler_view_Purchase;
    purchasesAdapter adapter;

    //Pulling From Database
    DatabaseReference dbOrderHistory, dbProduct;

    //Temporary Storing
    List<productClass> products;
    List<String> productsID;

    public PurchaseFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);

        //Identifying recycleView
        this.recycler_view_Purchase = view.findViewById(R.id.recycler_view_Purchase);
        this.recycler_view_Purchase.setHasFixedSize(true);
        this.recycler_view_Purchase.setLayoutManager(new LinearLayoutManager(getContext()));
        this.context = getActivity();

        //Identifying User
        this.pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", null);

        //DB Reference
        this.dbOrderHistory = FirebaseDatabase.getInstance().getReference("Order History");
        this.dbProduct = FirebaseDatabase.getInstance().getReference("Product");

        //Other required variables
        this.products = new ArrayList<>();
        this.productsID = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayProduct();
    }

    private void displayProduct() {
        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> itemList) {
                if (!productsID.isEmpty()) {
                    for (final String item : itemList) {
                        dbProduct.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(item)) {
                                        productClass productClass = dataSnapshot.child(item).getValue(productClass.class);
                                        products.add(productClass);
                                    }
                                }
                                adapter = new purchasesAdapter(getActivity(), products);
                                recycler_view_Purchase.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Debug: onCancelled (dbWatchList)", databaseError.getMessage());
                            }
                        });
                    }
                } else {
                    adapter = new purchasesAdapter(getActivity(), products);
                    recycler_view_Purchase.setAdapter(adapter);
                }
            }
        });
    }

    private void readData (final PurchaseFragment.FirebaseCallback firebaseCallback){
        dbOrderHistory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userIdentity)) {
                    for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                        String gd_ID = snapshot.child("oh_pro_ID").getValue().toString();
                        productsID.add(gd_ID);
                    }
                    firebaseCallback.onCallback(productsID);
                } else {
                    firebaseCallback.onCallback(productsID);
                    Log.d("Debug: No Previous Purchase", productsID.toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Debug: onCancelled (dbWatchList)", databaseError.getMessage());
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(List<String> itemList);
    }
}
