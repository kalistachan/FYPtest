package com.example.fyptest.Seller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.loginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class fragment_main extends Fragment {
    SharedPreferences preferences;
    String userIdentity;
    Context context;

    RecyclerView recyclerView;
    DatabaseReference dbProduct;

    List<productClass> productList;
    MainFragmentAdapter adapter;

    public fragment_main() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_main, container, false);
        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.context = getContext();

        //Identifying User
        try {
            this.preferences = getContext().getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = preferences.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(getContext(), loginActivity.class));
        }

        this.productList = new ArrayList<>();
        this.dbProduct = FirebaseDatabase.getInstance().getReference("Product");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayProduct();
    }

    private void displayProduct() {
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("pro_s_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                        if (!snapshot.child("pro_Status").getValue().toString().equalsIgnoreCase("sold")) {
                            productClass product = snapshot.getValue(productClass.class);
                            productList.add(product);
                        }
                    }
                }
                adapter = new MainFragmentAdapter(getActivity(), productList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
