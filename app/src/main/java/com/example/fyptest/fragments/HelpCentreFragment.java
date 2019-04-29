package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyptest.Adapters.NotificationAdapter;
import com.example.fyptest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HelpCentreFragment extends Fragment {
    //Get user Identity
    SharedPreferences pref;
    String userIdentity;

    //RecyclerView Items
    RecyclerView recycler_view_FAQ;
    NotificationAdapter adapter;

    //Database FAQ
    DatabaseReference dbDAQ;



    public HelpCentreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        DatabaseReference dbDAQ = FirebaseDatabase.getInstance().getReference("FAQ");

        return view;
    }

    private void getFAQ() {

    }
}
