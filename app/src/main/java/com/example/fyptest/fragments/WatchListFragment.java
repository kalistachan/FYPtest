package com.example.fyptest.fragments;

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

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WatchListFragment extends Fragment {

    SharedPreferences pref;
    RecyclerView recycler_view_WatchList;
    DatabaseReference db1,db2;
    CustomAdapter mAdapter;
    List<productClass> prodList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_list, container, false);

        recycler_view_WatchList = view.findViewById(R.id.recycler_view_WatchList);
        recycler_view_WatchList.setHasFixedSize(true);
        recycler_view_WatchList.setLayoutManager(new LinearLayoutManager(getContext()));

        prodList = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void displayProduct(){
        db1 = FirebaseDatabase.getInstance().getReference("Product");
        //db2 = FirebaseDatabase.getInstance().getReference("Watch List").child();
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                for (DataSnapshot snapshot1: dataSnapshot1.getChildren()) {
//                    final String getItem = snapshot1.getValue().toString();
//                    db1.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
//                            for (DataSnapshot snapshot2 : dataSnapshot2.getChildren()) {
//                                if (getItem.equals(snapshot2.getValue().toString())) {
//
//                                }
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                        }
//                    });
                    productClass product = snapshot1.getValue(productClass.class);
                    prodList.add(product);
                }
                mAdapter = new CustomAdapter(getContext(), prodList);
                recycler_view_WatchList.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
