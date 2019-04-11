package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fyptest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    ArrayList<String> list;
    SharedPreferences pref;
    String getStr;

    EditText editText1, email, contactNo, Password, confirmPassword, address, ccExpiryDate, ccNum, ccCVV;

//    To save data to SharePreferences
//    SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
//             editor.putString(saveKey, stringToSave);
//             editor.apply();
//
//    To load the data at a later time
//    SharedPreferences prefs = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//    String loadedString = prefs.getString(saveKey, null);
//             txt_2.setText(loadedString);


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        list = new ArrayList<>();

        pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        getStr = pref.getString("userID", "UNKNOWN");

        editText1 = (EditText) view.findViewById(R.id.editText1);
        email = (EditText) view.findViewById(R.id.email);
        contactNo = (EditText) view.findViewById(R.id.contactNo);
        Password = (EditText) view.findViewById(R.id.Password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        address = (EditText) view.findViewById(R.id.address);
        ccExpiryDate = (EditText) view.findViewById(R.id.ccExpiryDate);
        ccNum = (EditText) view.findViewById(R.id.ccNum);
        ccCVV = (EditText) view.findViewById(R.id.ccCVV);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    public void getData(final String userID) {
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("User").child(userID);
        DatabaseReference dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(userID);
        DatabaseReference dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(userID);

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("userID").getValue().toString().equals(userID)) {
                        list.add(snapshot.child("email").getValue().toString());
                        list.add(snapshot.child("contactNum").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCusInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("cus_ID").getValue().toString().equals(userID)) {
                        list.add(snapshot.child("cus_fName").getValue().toString());
                        list.add(snapshot.child("cus_lName").getValue().toString());
                        list.add(snapshot.child("cus_shippingAddress").getValue().toString());
                        list.add(snapshot.child("cus_userType").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("cc_cus_ID").getValue().toString().equals(userID)) {
                        list.add(snapshot.child("cc_Num").getValue().toString());
                        list.add(snapshot.child("cc_ExpiryDate").getValue().toString());
                        list.add(snapshot.child("cc_CVNum").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;

            }
        });
    }
}
