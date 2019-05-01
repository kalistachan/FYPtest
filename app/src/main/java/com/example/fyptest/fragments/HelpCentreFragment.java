package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyptest.Adapters.FAQ_Adapter;
import com.example.fyptest.Adapters.NotificationAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.faqClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HelpCentreFragment extends Fragment {
    //Get user Identity
    SharedPreferences pref;
    String userIdentity;

    //RecyclerView Items
    RecyclerView recycler_view_FAQ;
    FAQ_Adapter adapter;

    //Database FAQ
    List<faqClass> infoList;

    public HelpCentreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFAQ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_help_centre, container, false);
        //Identifying recycleView
        this.recycler_view_FAQ = view.findViewById(R.id.recycler_view_FAQ);
        this.recycler_view_FAQ.setHasFixedSize(true);
        this.recycler_view_FAQ.setLayoutManager(new LinearLayoutManager(getContext()));

        //Identifying User
        this.pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", null);

        //Connect Database
        this.infoList = new ArrayList<>();

        return view;
    }

    private void getFAQ() {
        DatabaseReference dbDAQ = FirebaseDatabase.getInstance().getReference("FAQ");
        dbDAQ.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    infoList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        faqClass faqClass = snapshot.getValue(faqClass.class);
                        infoList.add(faqClass);
                    }
                    adapter = new FAQ_Adapter(getActivity(), infoList);
                    recycler_view_FAQ.setAdapter(adapter);
                } else if (!dataSnapshot.hasChildren()) {
                    adapter = new FAQ_Adapter(getActivity(), infoList);
                    recycler_view_FAQ.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
