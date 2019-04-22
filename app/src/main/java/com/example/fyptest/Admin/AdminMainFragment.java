package com.example.fyptest.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminMainFragment extends Fragment {
    SharedPreferences preferences;
    String userIdentity;
    Context context;

    RecyclerView recyclerView;
    DatabaseReference dbProduct;

    List<productClass> productList;
    AdminAdapter adapter;

    public AdminMainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_main, container, false);
        this.recyclerView = view.findViewById(R.id.recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.context = getContext();

        this.preferences = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", null);

        this.productList = new ArrayList<>();
        this.dbProduct = FirebaseDatabase.getInstance().getReference("Product");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayProduct();
    }

    private void displayProduct() {
        dbProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    if (snapshot.child("pro_Status").getValue().toString().equalsIgnoreCase("pending")) {
                        productClass product = snapshot.getValue(productClass.class);
                        productList.add(product);
//                    }
                }
                adapter = new AdminAdapter(getActivity(), productList);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
