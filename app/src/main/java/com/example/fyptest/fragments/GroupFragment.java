package com.example.fyptest.fragments;


import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;


import com.example.fyptest.CustomAdapter;
import com.example.fyptest.GroupCustomAdapter;
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
    List<productClass> grpList;
    GroupCustomAdapter mAdapter;
    List<String> grpProdID;
    SharedPreferences pref;
    String userIdentity;

    public GroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.group, container, false);
        this.grpProdID = new ArrayList<>();
        mRecyclerView = groupView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.pref = getContext().getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", "UNKNOWN");
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayGroup();
    }

    public void displayGroup () {
        grpList = new ArrayList<>();
        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(final List<String> grpProdID) {
                if (!grpProdID.isEmpty()) {

                    for (final String item : grpProdID) {
                        DatabaseReference dbProd = FirebaseDatabase.getInstance().getReference("Product");
                        dbProd.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                                    if (productSnapshot.child("pro_ID").getValue().toString().equalsIgnoreCase(item)) {
                                        productClass product = productSnapshot.getValue(productClass.class);
                                        grpList.add(product);
                                    }
                                }
                                mAdapter = new GroupCustomAdapter(getActivity(), grpList);
                                mRecyclerView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } grpProdID.clear();
            }
        });
    }

    private void readData (final GroupFragment.FirebaseCallback firebaseCallback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot nestedsnapshot : snapshot.getChildren()) {
                        if (nestedsnapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                            String productID = nestedsnapshot.child("gd_pg_pro_ID").getValue().toString();
                            grpProdID.add(productID);
                            break;
                        }
                    }
                }
                firebaseCallback.onCallback1(grpProdID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback1(List<String> itemList);
    }

    public void checkingConditionForRemoval(final String productID, final String userID, final List<productClass> list, final Context context) {
        DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID);
        dbGroupDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                        String gd_ID = snapshot.child("gd_ID").getValue().toString();
                        removeGroupDetail(productID, gd_ID);
                        counter++;
                    } else if (!snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userID)){
                        counter++;
                    }
                }
                if (counter == 1) {
                    removeProductGroup(productID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeProductGroup(final String productID) {
        DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group").child(productID);
        dbProductGroup.removeValue();
    }

    private void removeGroupDetail(final String productID, final String groupDetailID) {
        DatabaseReference dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(productID).child(groupDetailID);
        Log.d("Count", dbGroupDetail.toString());
        dbGroupDetail.removeValue();
    }
}
