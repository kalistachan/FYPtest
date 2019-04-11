package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.fyptest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    SharedPreferences pref;
    String getStr;

    DatabaseReference dbUser, dbCusInfo, dbCC;

    EditText email, contactNo, Password, confirmPassword, address, ccExpiryDate, ccNum, ccCVV;

    TextView profileTitle1;

    Switch notification;

    Button update, logout;

    ArrayList<String> list;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        getStr = pref.getString("userID", "UNKNOWN");

        dbUser = FirebaseDatabase.getInstance().getReference("User").child(getStr);
        dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr);
        dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(getStr);

        email = (EditText) view.findViewById(R.id.email);
        contactNo = (EditText) view.findViewById(R.id.contactNo);
        Password = (EditText) view.findViewById(R.id.Password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        address = (EditText) view.findViewById(R.id.address);
        ccExpiryDate = (EditText) view.findViewById(R.id.ccExpiryDate);
        ccNum = (EditText) view.findViewById(R.id.ccNum);
        ccCVV = (EditText) view.findViewById(R.id.ccCVV);

        notification = (Switch) view.findViewById(R.id.switch1);
        update = (Button) view.findViewById(R.id.updateBtn);
        logout = (Button) view.findViewById(R.id.logoutBtn);

        profileTitle1 = (TextView) view.findViewById(R.id.profileTitle1);

        notification.setText(notification.getTextOn());

        list = new ArrayList<>();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactNo = (EditText) view.findViewById(R.id.contactNo);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email.setHint(dataSnapshot.child("email").getValue().toString());
                list.add("email");
                contactNo.setHint(dataSnapshot.child("contactNum").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCusInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fName = dataSnapshot.child("cus_fName").getValue().toString();
                String lName = dataSnapshot.child("cus_lName").getValue().toString();
                profileTitle1.setText(lName + " " + fName + " Information");
                String add = dataSnapshot.child("cus_shippingAddress").getValue().toString();
                String postal = dataSnapshot.child("cus_postalCode").getValue().toString();
                address.setHint(add + " Singapore " + postal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ccNum.setHint(dataSnapshot.child("cc_Num").getValue().toString());
                ccExpiryDate.setHint(dataSnapshot.child("cc_ExpiryDate").getValue().toString());
                ccCVV.setHint(dataSnapshot.child("cc_CVNum").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }
}
