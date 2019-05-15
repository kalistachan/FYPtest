package com.example.fyptest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fyptest.Adapters.watchListAdapter;
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

public class WatchListFragment extends Fragment {
    //Get user Identity
    SharedPreferences pref;
    String userIdentity;

    //RecyclerView Items
    RecyclerView recycler_view_WatchList;
    watchListAdapter adapter;

    //Database
    DatabaseReference dbProduct, dbWatchList;

    //Storing list used for later callbacks
    List<productClass> productList;
    List<String> watchListProd;

    BottomNavigationView navigation;

    public WatchListFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        //Identifying recycleView
        this.recycler_view_WatchList = view.findViewById(R.id.recycler_view_WatchList);
        this.recycler_view_WatchList.setHasFixedSize(true);
        this.recycler_view_WatchList.setLayoutManager(new LinearLayoutManager(getContext()));

        //Identifying User
        try {
            this.pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
            this.userIdentity = pref.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(getContext(), loginActivity.class));
        }

        //DB Reference
        this.dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
        this.dbProduct = FirebaseDatabase.getInstance().getReference("Product");

        //Other required variables
        this.productList = new ArrayList<>();
        this.watchListProd = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayWatchProducts();
    }

    private void displayWatchProducts() {
        //Extracting productID from watchList to save under a string list
        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(List<String> itemList) {
                if (!watchListProd.isEmpty()) {
                    productList.clear();
                    for (final String item : watchListProd) {
                        dbProduct.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (snapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(item)) {
                                        productClass product = snapshot.getValue(productClass.class);
                                        productList.add(product);
                                    }
                                }
                                adapter = new watchListAdapter(getActivity(), productList);
                                recycler_view_WatchList.setAdapter(adapter);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("Debug: onCancelled (dbProduct)", databaseError.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private void readData (final FirebaseCallback firebaseCallback){
        dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userIdentity)) {
                    for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                        watchListProd.add(snapshot.child("wl_pro_ID").getValue().toString());
                    }
                    firebaseCallback.onCallback1(watchListProd);
                } else if (!dataSnapshot.hasChild(userIdentity)){
                    firebaseCallback.onCallback1(watchListProd);
                    Log.d("Debug: Null WatchList", watchListProd.toString());
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
        void onCallback1(List<String> itemList);
    }

}
