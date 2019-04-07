package com.example.fyptest.fragments;

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
        return inflater.inflate(R.layout.fragment_group, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       // MainActivity mA = new MainActivity();
        displayProduct();
    }

    public void displayProduct () {
        databaseProduct = FirebaseDatabase.getInstance().getReference("product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //prodList.clear();

                simpleList = (ListView) getActivity().findViewById(R.id.simpleListView);
                prodList = new ArrayList<>();
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
                    Log.d("PRODUCT NAME", "FROM DB " + product.getProdName());
                    prodList.add(product);
                }

                CustomAdapter adapter = new CustomAdapter(getActivity().getApplicationContext(), prodList);
                Log.d("ADAPTER", "ADAP ACTIVITY: " + getActivity());
                Log.d("PROD LIST", "PROD LIST: " + prodList.get(0).getProdName());
                simpleList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
