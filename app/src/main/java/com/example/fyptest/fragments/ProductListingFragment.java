package com.example.fyptest.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.example.fyptest.database.Product;
import com.example.fyptest.database.groupDetailClass;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    String prodGrpId;
    Date pgDateCreated;
    Date pgDateEnd;
    boolean[] abc;
    Date gdJoinDate;
    String gdCusID;

    SharedPreferences prefs;

    public ProductListingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_productlisting, container, false);

        mRecyclerView = groupView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayProduct();
        abc = new boolean[1];

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void displayProduct () {
        prodList = new ArrayList<>();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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

    public int ShowDialog(Context context, final String prodID, String prodName) {
        final int[] qtyChosen = new int[1];
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
                qtyChosen[0] = qtyChosenVal;
            }
        });

        popDialog.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                qtyChosen[0] = 0;
            }
        });

        AlertDialog alertdialog = popDialog.create();
        alertdialog.show();
        Log.d("dialogstatus ", "value: " + qtyChosen);
        return qtyChosen[0];
    }

    private void insertProductGroup (String prodID) {
        /* if product is in product group table, get pg_id, else insert new product group (pg_id, pg_createdDate, prodID) and return pg_id,
            then call insertCustGroupDetails (pg_id)*/
        pgDateCreated = Calendar.getInstance().getTime();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product Group");
        prodGrpId = databaseProduct.push().getKey();
        productGroupClass productGroup = new productGroupClass(prodGrpId, pgDateCreated, null, prodID);
        databaseProduct.child(prodGrpId).setValue(productGroup);
        insertCustGroupDetails(prodGrpId);
    }

    public void insertCustGroupDetails (String prodGroupId) {
        /* first, check if cust group detail table has this pg_id, if yes then dont insert, else insert (gd_id, gd_joinDate, cus_ID, pg_id)*/
        databaseProduct = FirebaseDatabase.getInstance().getReference("Group Detail");
        gdJoinDate = Calendar.getInstance().getTime();
        prefs = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
        gdCusID = prefs.getString("userID", null);
        groupDetailClass groupDetail =  new groupDetailClass(gdJoinDate, qtyChosenVal, gdCusID, prodGroupId);
        databaseProduct.setValue(groupDetail);
    }
}

