package com.example.fyptest.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ImageViewHolder> {
    Context context;
    List<productClass> productList;
    SharedPreferences preferences;
    String userIdentity;

    private static final String TAG = "Debug: Watch List Adapter";

    public watchListAdapter(Context context, List<productClass> productList) {
        this.productList = productList;
        this.context = context;
        this.preferences = context.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.watchlist_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder viewHolder, final int position) {
        final productClass uploadCurrent = productList.get(position);
        final String prodID = uploadCurrent.getPro_ID();
        final String prodName = uploadCurrent.getPro_name();
        final String retailPrice = uploadCurrent.getPro_retailPrice();
        final String minOrderQtySellPrice = uploadCurrent.getPro_minOrderQtySellPrice();
        final String maxOrderQtySellPrice = uploadCurrent.getPro_maxOrderQtySellPrice();
        final String shippingFee = uploadCurrent.getPro_shippingCost();
        final String freeShipping = uploadCurrent.getPro_freeShippingAt();
        final String targetQty = uploadCurrent.getPro_targetQuantity();
        final String timeRemain = uploadCurrent.getPro_durationForGroupPurchase();
        viewHolder.prodNameViewName.setText(prodName);
        viewHolder.prodPriceViewName.setText("$" + retailPrice);
        viewHolder.shipPriceView.setText("$" + shippingFee);
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.image_view_upload);

        viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromRecycleView(position);
                removeFromWatchList(prodID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void removeFromWatchList(String prodID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List").child(userIdentity).child(prodID);
        db.removeValue();

    }

    private void removeItemFromRecycleView(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView image_view_upload;
        TextView prodNameViewName, prodPriceViewName, shipPriceView, shippingFee;
        LinearLayout parent_layout;
        Button btnRemove, btnAdd;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view_upload = itemView.findViewById(R.id.image_view_upload);
            prodNameViewName = itemView.findViewById(R.id.prodNameViewName);
            prodPriceViewName = itemView.findViewById(R.id.prodPriceViewName);
            shipPriceView = itemView.findViewById(R.id.shipPriceView);
            shippingFee = itemView.findViewById(R.id.shippingFee);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }
}