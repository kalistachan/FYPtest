package com.example.fyptest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fyptest.Adapters.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_spinner_item;
import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    RecyclerView mRecyclerView;
    Context mContext;
    SharedPreferences prefs;
    String userIdentity;
    List<productClass> mSearch;
    CustomAdapter mAdapter;
    Spinner category;
    Spinner sortBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View groupView = inflater.inflate(R.layout.fragment_searchcriteria, container, false);
        this.mRecyclerView = groupView.findViewById(R.id.recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        this.mContext = getContext();

        this.prefs = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = prefs.getString("userID", "UNKNOWN");
        category = groupView.findViewById(R.id.spinnerCategory);
        sortBy = groupView.findViewById(R.id.spinnerSortBy);
        return groupView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        String query = arguments.getString("query");
        populateCategory();
        searchProduct(query);
    }

    private void populateCategory() {
        DatabaseReference prodType = FirebaseDatabase.getInstance().getReference("Product Type");
        prodType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> cat = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String categoryName = areaSnapshot.child("pt_Name").getValue().toString();
                    Log.d("cat", "value: " + categoryName);
                    cat.add(categoryName);
                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cat);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                category.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateSortBy() {
        List<String> sortByValues = new ArrayList<String>(Arrays.asList("Sort Price in Asc","Sort Price in Desc"));
        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortByValues);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBy.setAdapter(areasAdapter);
    }

    public void searchProduct (String inputQuery) {
        Log.d("query","search product value: " + inputQuery);
        mSearch = new ArrayList<>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Product");

        Query query = rootRef.orderByChild("pro_name").startAt(inputQuery).endAt(inputQuery + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSearch.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    productClass search = postSnapshot.getValue(productClass.class);
                    Log.d("search result ", " value in snapshot: " + search.getPro_name());
                    mSearch.add(search);
                }

                mAdapter = new CustomAdapter(getActivity(), mSearch);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
