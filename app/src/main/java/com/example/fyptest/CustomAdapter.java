package com.example.fyptest;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.fyptest.database.Product;
import com.example.fyptest.fragments.ProductListingFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {
    Context mContext;
    List<Product> productList;

    public CustomAdapter(Context applicationContext,  List<Product> productList) {
        this.mContext = applicationContext;
        this.productList = productList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Product uploadCurrent = productList.get(position);
        holder.prodTextName.setText(uploadCurrent.getProdName());
        holder.prodTextPrice.setText(uploadCurrent.getProdPrice());
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
         TextView prodTextName;
         TextView prodTextPrice;
         ImageView imageView;
         Button grpBtn;
         Button watchBtn;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            prodTextName = itemView.findViewById(R.id.prodNameViewName);
            prodTextPrice = itemView.findViewById(R.id.prodPriceViewName);
            imageView = itemView.findViewById(R.id.image_view_upload);
            grpBtn = itemView.findViewById(R.id.btn1);
            watchBtn = itemView.findViewById(R.id.btn2);
            final ProductListingFragment pl = new ProductListingFragment();

            String checkGroup ="exist";
            if (checkGroup == "exist") {
                grpBtn.setText("Join Group");
                grpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       pl.ShowDialog(mContext,itemView);
                    }
                });
            } else {
                grpBtn.setText("Create Group");
            }

            String checkWatch ="exist";
            if (checkWatch == "exist") {
                watchBtn.setText("In Watchlist");
                watchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                watchBtn.setText("Add to Watchlist");
            }
        }
    }

}
