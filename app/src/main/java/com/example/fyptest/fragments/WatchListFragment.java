package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.fyptest.Adapters.watchListAdapter;
import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.watchlistClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {

    SharedPreferences pref;
    String getUserID;

    RecyclerView recycler_view_WatchList;

    DatabaseReference dbProduct,dbUser, dbCustomer, dbWatchList;
    watchListAdapter adapter;

    List<productClass> productList;
    List<String> watchListProd;

    public WatchListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        //Identifying recycleView
        recycler_view_WatchList = view.findViewById(R.id.recycler_view_WatchList);
        recycler_view_WatchList.setHasFixedSize(true);
        recycler_view_WatchList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Identifying User
        pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);

        //Connecting DB Reference
        dbUser = FirebaseDatabase.getInstance().getReference("User");
        dbCustomer = FirebaseDatabase.getInstance().getReference("Customer Information");

        //Other required variables
        productList = new ArrayList<>();
        watchListProd = new ArrayList<>();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayProdusts();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void displayProdusts() {
        dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
        dbProduct = FirebaseDatabase.getInstance().getReference("Product");
        getUserID = pref.getString("userID", null);

        //Extracting productID from watchList to save under a string list
        dbWatchList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot watchlistSnapshot : dataSnapshot.getChildren()) {
                    final String productInWatchList = watchlistSnapshot.child("wl_pro_ID").getValue().toString();
                    Log.d("ValueCheck: ID of product in watch list snapShot", productInWatchList);
                    dbProduct.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                                Log.d("ValueCheck: ID of product in watch list snapShot", productInWatchList);
                                if (productSnapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(productInWatchList)) {
                                    productClass products = productSnapshot.getValue(productClass.class);
                                    productList.add(products);
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }
                adapter = new watchListAdapter(getActivity(), productList);
                recycler_view_WatchList.setAdapter(adapter);
                Log.d("ValueCheck: After For Loop in dataChange Class", productList.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.d("ValueCheck: After dataChange Class", productList.toString());

    }
}
