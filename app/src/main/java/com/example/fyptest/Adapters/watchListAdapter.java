package com.example.fyptest.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fyptest.CustomAdapter;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ViewHolder> {

    private static final String TAG = "watchListAdapter";

    List<Product> productList;
    Context context;

    public watchListAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_watch_list,viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");

//        Product uploadCurrent = productList.get(i);
//        viewHolder.prodNameViewName.setText(uploadCurrent.getProdName());
//        viewHolder.prodPriceViewName.setText(uploadCurrent.getProdPrice());
//        Picasso.get()
//                .load(uploadCurrent.getImageUrl())
//                .fit()
//                .centerCrop()
//                .into(viewHolder.image_view_upload);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_view_upload;
        TextView prodNameViewName, prodPriceViewName;
        LinearLayout parent_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view_upload = itemView.findViewById(R.id.image_view_upload);
            prodNameViewName = itemView.findViewById(R.id.prodNameViewName);
            prodPriceViewName = itemView.findViewById(R.id.prodPriceViewName);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}