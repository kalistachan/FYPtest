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

import com.example.fyptest.Adapters.NotificationAdapter;
import com.example.fyptest.Adapters.watchListAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    //Get user Identity
    SharedPreferences pref;
    String userIdentity;

    //RecyclerView Items
    RecyclerView recycler_view_Notification;
    NotificationAdapter adapter;

    List<String> watchListProd;
    List<productClass> productGroupItem;

    public NotificationsFragment() {

    }

    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        //Identifying recycleView
        this.recycler_view_Notification = view.findViewById(R.id.recycler_view_Notification);
        this.recycler_view_Notification.setHasFixedSize(true);
        this.recycler_view_Notification.setLayoutManager(new LinearLayoutManager(getContext()));

        //Identifying User
        this.pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", null);
        this.watchListProd = new ArrayList<>();
        this.productGroupItem = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(List<String> itemList) {
                productGroupItem.clear();
                if (!watchListProd.isEmpty()) {
                    for (final String item : watchListProd) {
                        DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group");
                        dbProductGroup.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(item)) {
                                    DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(item);
                                    dbGroupDetail.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            boolean checkUser = false;
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                                    checkUser = true;
                                                }
                                            }
                                            if (!checkUser) {
                                                DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product");
                                                dbProductGroup.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            if (snapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(item)) {
                                                                productClass productClass = snapshot.getValue(productClass.class);
                                                                productGroupItem.add(productClass);
                                                            }
                                                        }
                                                        adapter = new NotificationAdapter(getActivity(), productGroupItem);
                                                        recycler_view_Notification.setAdapter(adapter);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.d("Debug: onCancelled (dbProduct)", databaseError.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else if (watchListProd.isEmpty()) {
                    productGroupItem.clear();
                    adapter = new NotificationAdapter(getActivity(), productGroupItem);
                    recycler_view_Notification.setAdapter(adapter);
                }
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback1(List<String> itemList);
    }

    private void readData (final FirebaseCallback firebaseCallback){
        DatabaseReference dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List").child(userIdentity);
        dbWatchList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                watchListProd.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    watchListProd.add(snapshot.getKey());
                }
                Log.d("Test", "read me");
                firebaseCallback.onCallback1(watchListProd);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
