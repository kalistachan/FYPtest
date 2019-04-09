package com.example.fyptest.fragments;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends Fragment {

    DatabaseReference databaseProduct;
    List<Product> prodList;
    ListView simpleList;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_group, container, false);
        simpleList = (ListView) groupView.findViewById(R.id.simpleListView);
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
          displayProduct();
    }

    public void displayProduct () {
        databaseProduct = FirebaseDatabase.getInstance().getReference("product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                prodList = new ArrayList<>();
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    prodList.add(product);
                }
                CustomAdapter adapter = new CustomAdapter(getActivity(), R.layout.fragment_group, prodList);
                simpleList.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }




        });

    }


}
