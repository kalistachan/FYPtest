package com.example.fyptest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    public void onBindViewHolder(ImageViewHolder holder, final int position) {
        final Product uploadCurrent = productList.get(position);
        final String prodID = uploadCurrent.getProdID();
        final ProductListingFragment pl = new ProductListingFragment();
        holder.prodTextName.setText(uploadCurrent.getProdName());
        holder.prodTextPrice.setText(uploadCurrent.getProdPrice());
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        if (pl.checkProductGroup(prodID)== false) {
            holder.grpBtn.setText("Create Group");
            holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pl.ShowDialog(mContext);
                    pl.insertProductGroup(prodID);
                }
            });
        } else {
            holder.grpBtn.setText("Join Group");
        }
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
            grpBtn = itemView.findViewById(R.id.purchaseStatus);
            watchBtn = itemView.findViewById(R.id.btn2);

        }
    }

}
