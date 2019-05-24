package com.example.fyptest.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fyptest.Adapters.CustomAdapter;
import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.loginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static android.R.layout.simple_spinner_item;
import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    RecyclerView mRecyclerView;
    Context mContext;
    SharedPreferences prefs;
    String userIdentity, catSelected;
    int sortChoice;
    List<productClass> mSearch, mAfterSort;
    CustomAdapter mAdapter;
    Spinner category, sortBy;
    Button submitBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_searchcriteria, container, false);
        this.mRecyclerView = groupView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.mContext = getContext();

        //Identifying User
        try {
            this.prefs = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = prefs.getString("userID", "UNKNOWN");
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(getContext(), loginActivity.class));
        }

        category = groupView.findViewById(R.id.spinnerCategory);
        sortBy = groupView.findViewById(R.id.spinnerSortBy);
        submitBtn = groupView.findViewById(R.id.buttonSubmit);

        mSearch = new ArrayList<>();
        mAfterSort = new ArrayList<>();
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        String query = arguments.getString("query");
        searchProduct(query);
        populateCategory();
        populateSortBy();
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                catSelected = category.getSelectedItem().toString();
                sortChoice = sortBy.getSelectedItemPosition();
                mAfterSort.clear();
                try {
                    for (int i = 0; i < mSearch.size(); i++) {
                        if (catSelected.equalsIgnoreCase(mSearch.get(i).getPro_productType())) {
                            mAfterSort.add(mSearch.get(i));
                            if (sortChoice == 0) {
                                //sort by lowest price
                                Collections.sort(mAfterSort, new Comparator<productClass>() {

                                    @Override
                                    public int compare(productClass o1, productClass o2) {
                                        return (int) (((Float) Float.parseFloat(o1.getPro_maxOrderQtySellPrice())) - ((Float) Float.parseFloat(o2.getPro_maxOrderQtySellPrice())));
                                    }
                                });
                            } else if (sortChoice == 1) {
                                //sort by highest price
                                Collections.sort(mAfterSort, new Comparator<productClass>() {

                                    @Override
                                    public int compare(productClass o1, productClass o2) {
                                        return (int) (((Float) Float.parseFloat(o2.getPro_maxOrderQtySellPrice())) - ((Float) Float.parseFloat(o1.getPro_maxOrderQtySellPrice())));
                                    }
                                });
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("number format error");
                }

                mAdapter = new CustomAdapter(getActivity(), mAfterSort);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    private void populateCategory() {
        DatabaseReference prodType = FirebaseDatabase.getInstance().getReference("Product Type");
        prodType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> cat = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String categoryName = areaSnapshot.child("pt_Name").getValue().toString();

                    cat.add(categoryName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cat);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(areasAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSortBy() {
        List<String> sortByValues = new ArrayList<String>();
        sortByValues.add("Lowest Price");
        sortByValues.add("Highest Price");
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, sortByValues);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(areasAdapter);
    }

    public void searchProduct (final String inputQuery) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Product");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

        Query query = rootRef.orderByChild("pro_name").startAt(inputQuery).endAt(inputQuery + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    searchProductDetails(inputQuery);
                } else {
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            mSearch.clear();
                            for (DataSnapshot postSnapshot2 : dataSnapshot2.getChildren()) {
                                if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("seller")) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (postSnapshot.child("pro_s_ID").getValue().toString().equals(userIdentity))
                                        {
                                            productClass search = postSnapshot.getValue(productClass.class);
                                            mSearch.add(search);
                                        }
                                    }
                                    mAdapter = new CustomAdapter(getActivity(), mSearch);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("customer")) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (postSnapshot.child("pro_Status").getValue().toString().equals("approved"))
                                        {
                                            productClass search = postSnapshot.getValue(productClass.class);
                                            mSearch.add(search);
                                        }
                                    }
                                    mAdapter = new CustomAdapter(getActivity(), mSearch);
                                    mRecyclerView.setAdapter(mAdapter);
                                } else if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("admin")) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (postSnapshot.child("pro_Status").getValue().toString().equals("pending"))
                                        {
                                            productClass search = postSnapshot.getValue(productClass.class);
                                            mSearch.add(search);
                                        }
                                    }
                                    mAdapter = new CustomAdapter(getActivity(), mSearch);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchProductDetails (final String inputQuery) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Product");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

        Query query = rootRef.orderByChild("pro_description").startAt(inputQuery).endAt(inputQuery + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        mSearch.clear();
                        for (DataSnapshot postSnapshot2 : dataSnapshot2.getChildren()) {
                            if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("seller")) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.child("pro_s_ID").getValue().toString().equals(userIdentity))
                                    {
                                        productClass search = postSnapshot.getValue(productClass.class);
                                        mSearch.add(search);
                                    }
                                }
                                mAdapter = new CustomAdapter(getActivity(), mSearch);
                                mRecyclerView.setAdapter(mAdapter);
                            } else if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("customer")) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.child("pro_Status").getValue().toString().equals("approved"))
                                    {
                                        productClass search = postSnapshot.getValue(productClass.class);
                                        mSearch.add(search);
                                    }
                                }
                                mAdapter = new CustomAdapter(getActivity(), mSearch);
                                mRecyclerView.setAdapter(mAdapter);
                            } else if (postSnapshot2.child("userID").getValue().toString().equalsIgnoreCase(userIdentity) && postSnapshot2.child("userType").getValue().toString().equalsIgnoreCase("admin")) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    if (postSnapshot.child("pro_Status").getValue().toString().equals("pending"))
                                    {
                                        productClass search = postSnapshot.getValue(productClass.class);
                                        mSearch.add(search);
                                    }
                                }
                                mAdapter = new CustomAdapter(getActivity(), mSearch);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}


