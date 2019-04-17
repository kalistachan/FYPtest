package com.example.fyptest.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.example.fyptest.database.productClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ViewHolder> {

    Context context;
    List<productClass> productList;

    private static final String TAG = "watchListAdapter";

    public watchListAdapter(Context context, List<productClass> productList) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.watchlist_listing, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {}

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_view_upload;
        TextView prodNameViewName, prodPriceViewName, shipPriceView;
        LinearLayout parent_layout;
        Button btnRemove, btnAdd;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view_upload = itemView.findViewById(R.id.image_view_upload);
            prodNameViewName = itemView.findViewById(R.id.prodNameViewName);
            prodPriceViewName = itemView.findViewById(R.id.prodPriceViewName);
            shipPriceView = itemView.findViewById(R.id.shipPriceView);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnAdd = itemView.findViewById(R.id.btnAdd);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}