package com.example.fyptest.fragments;


import android.content.Context;
import android.content.Intent;
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
import com.example.fyptest.database.notificationClass;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.example.fyptest.loginActivity;
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
    List<notificationClass> notificationItem;
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
        try {
            this.pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
            this.userIdentity = pref.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(getContext(), loginActivity.class));
        }

        this.watchListProd = new ArrayList<>();
        this.notificationItem = new ArrayList<>();
        this.productGroupItem = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Notification").child(userIdentity);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    notificationClass notificationClass = snapshot.getValue(notificationClass.class);
                    notificationItem.add(notificationClass);
                }
                adapter = new NotificationAdapter(getActivity(), notificationItem);
                recycler_view_Notification.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
