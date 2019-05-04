package com.example.fyptest.fragments;


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

import com.example.fyptest.Adapters.GroupCustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class GroupFragment extends Fragment {
    RecyclerView mRecyclerView;
    GroupCustomAdapter mAdapter;

    List<productClass> grpList;
    List<String> grpProdID;

    SharedPreferences pref;
    String userIdentity;

    public GroupFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayGroup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.group, container, false);
        this.mRecyclerView = groupView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.pref = getContext().getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", "UNKNOWN");

        this.grpList = new ArrayList<>();
        this.grpProdID = new ArrayList<>();

        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void displayGroup () {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String productID = snapshot.child("pro_ID").getValue().toString();
                    readData(new FirebaseCallback() {
                        @Override
                        public void onCallback1(List<String> itemList) {
                            for (String item : grpProdID) {
                                if (item.equalsIgnoreCase(productID)) {
                                    productClass productClass = snapshot.getValue(productClass.class);
                                    grpList.add(productClass);
                                    break;
                                }
                            }
                        }
                    });
                }
                mAdapter = new GroupCustomAdapter(getContext(), grpList);
                mRecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readData (final GroupFragment.FirebaseCallback firebaseCallback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot nestedSnapshot : snapshot.getChildren()) {
                        if (nestedSnapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                            grpProdID.add(nestedSnapshot.child("gd_pg_pro_ID").getValue().toString());
                            break;
                        }
                    }
                }
                Log.d("12345", grpProdID.toString());
                firebaseCallback.onCallback1(grpProdID);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback1(List<String> itemList);
    }
}
