package com.example.fyptest.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductListingFragment extends Fragment {
    RecyclerView mRecyclerView;
    CustomAdapter mAdapter;
    DatabaseReference databaseProduct;
    List<Product> prodList;
    Context activity;
    int qtyChosenVal;
    int finalQtyChosenVal;
    TextView qtyText;

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
          displayProduct();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    public void displayProduct () {
        prodList = new ArrayList<>();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prodList = new ArrayList<>();
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()){
                    Product product = productSnapshot.getValue(Product.class);
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

    public void ShowDialog(Context context, View view) {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(context);

        LinearLayout linear = new LinearLayout(context);

        linear.setOrientation(LinearLayout.VERTICAL);
        qtyText = new TextView(context);
        qtyText.setPadding(10, 10, 10, 10);

        final SeekBar seek = new SeekBar(context);
        seek.setMax(1000);
        linear.addView(seek);
        linear.addView(qtyText);

        popDialog.setView(linear);

        popDialog.setTitle("Please Select Product Quantity ");

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Do something here with new value
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
                finalQtyChosenVal = seek.getProgress();
                Log.d("chosen qty", "value: " + finalQtyChosenVal);
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
}

