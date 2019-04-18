package com.example.fyptest.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.groupDetailClass;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


public class ProductListingFragment extends Fragment {
    RecyclerView mRecyclerView;
    CustomAdapter mAdapter;
    DatabaseReference databaseProduct;
    List<productClass> prodList;
    Context mContext;
    int qtyChosenVal;
    TextView qtyText;
    String gdCusID, prodGrpId;
    Date pgDateCreated, gdJoinDate;
    boolean[] abc;
    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_productlisting, container, false);
        this.mRecyclerView = groupView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        abc = new boolean[1];
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        abc = new boolean[1];
        displayProduct();
    }

    public void displayProduct () {
        prodList = new ArrayList<>();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prodList.clear();
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    productClass product = productSnapshot.getValue(productClass.class);
                    prodList.add(product);
                }
                mAdapter = new CustomAdapter(getActivity(), prodList);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void ShowDialog(Context context, final String prodID, String prodName) {
        mContext = context;
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(mContext);
        LinearLayout linear = new LinearLayout(mContext);

        linear.setOrientation(LinearLayout.VERTICAL);
        qtyText = new TextView(mContext);
        qtyText.setPadding(10, 10, 10, 10);
        qtyText.setGravity(Gravity.CENTER_HORIZONTAL);

        final SeekBar seek = new SeekBar(mContext);
        seek.setMax(10);
        linear.addView(seek);
        linear.addView(qtyText);

        popDialog.setView(linear);

        popDialog.setTitle("Please Select Quantity for " + prodName);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qtyChosenVal = progress;
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                qtyText.setText(qtyChosenVal + "/" + seekBar.getMax());
            }
        });

        popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                insertProductGroup(prodID);
            }
        });

        popDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertdialog = popDialog.create();
        alertdialog.show();
    }

    private void insertProductGroup (String prodID) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("d-MM-YYYY HH:MM");
        String string_pgDateCreated = df.format(c.getTime());
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product Group");
        productGroupClass productGroup = new productGroupClass(prodID, pgDateCreated, null, string_pgDateCreated);
        databaseProduct.child(prodID).setValue(productGroup);
        insertCustGroupDetails(prodID);
    }

    private void insertCustGroupDetails (String prodGroupId) {
        databaseProduct = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodGroupId);
        String pg_ID = databaseProduct.push().getKey();
        gdJoinDate = Calendar.getInstance().getTime();
        prefs = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
        gdCusID = prefs.getString("userID", null);
        groupDetailClass groupDetail =  new groupDetailClass(pg_ID, gdJoinDate, qtyChosenVal, prodGroupId, gdCusID);
        databaseProduct.child(pg_ID).setValue(groupDetail);
    }
}

