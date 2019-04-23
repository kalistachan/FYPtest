package com.example.fyptest.Seller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.AddProductFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainFragmentAdapter extends RecyclerView.Adapter<MainFragmentAdapter.ImageViewHolder> {
    Context mContext;
    List<productClass> productList;

    SharedPreferences preferences;
    String userIdentity;

    public MainFragmentAdapter(Context applicationContext,  List<productClass> productList) {
        this.mContext = applicationContext;
        this.productList = productList;

        this.preferences = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.seller_product_listing, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {
        final productClass uploadCurrent = productList.get(i);
        final String productID = uploadCurrent.getPro_ID();
        final String productName = uploadCurrent.getPro_name();
        final String productPrice = "$" + uploadCurrent.getPro_maxOrderQtySellPrice();
        final String productDuration = "Requires " + uploadCurrent.getPro_durationForGroupPurchase()  + " Days";
        final String productTargetQuantity = "0/" + uploadCurrent.getPro_targetQuantity();
        final String productShippingFee = "Shipping Fee : $" +uploadCurrent.getPro_shippingCost();
        final String freeShipping = uploadCurrent.getPro_freeShippingAt();
        final String productStatus = uploadCurrent.getPro_Status().toUpperCase();
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(imageViewHolder.imageView);

        imageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToProductView(mContext,productID);
            }
        });

        imageViewHolder.textViewProductStatus.setText(productStatus);
        imageViewHolder.textViewProductName.setText(productName);
        imageViewHolder.textViewPrice.setText(productPrice);
        imageViewHolder.durationValue.setText(productDuration);
        imageViewHolder.textViewTargetQuantity.setText(productTargetQuantity);
        imageViewHolder.shippingFee.setText(productShippingFee);

        if (freeShipping.isEmpty() || Integer.parseInt(freeShipping) == 0) {
            String setText = "Eligible for free Shipping : NA";
            imageViewHolder.freeShippingFee.setText(setText);
        } else {
            String setText = "Eligible for free Shipping : If order $" + freeShipping + " or more";
            imageViewHolder.freeShippingFee.setText(setText);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductStatus, textViewProductName, textViewPrice, durationValue, textViewTargetQuantity, shippingFee, freeShippingFee;
        ImageView imageView;
        Button btnEdit, btnRemove;

        public ImageViewHolder(View view) {
            super(view);
            this.textViewProductStatus = (TextView) view.findViewById(R.id.textViewProductStatus);
            this.textViewProductName = (TextView) view.findViewById(R.id.textViewProductName);
            this.textViewPrice = (TextView) view.findViewById(R.id.textViewPrice);
            this.durationValue = (TextView) view.findViewById(R.id.durationValue);
            this.textViewTargetQuantity = (TextView) view.findViewById(R.id.textViewTargetQuantity);
            this.shippingFee = (TextView) view.findViewById(R.id.shippingFee);
            this.freeShippingFee = (TextView) view.findViewById(R.id.freeShippingFee);
            this.imageView = (ImageView) view.findViewById(R.id.image_view_upload);
            this.btnEdit = (Button) view.findViewById(R.id.button5);
            this.btnRemove = (Button) view.findViewById(R.id.button4);
        }
    }

    public void swapToProductView(Context mContext, String prodID) {
        Activity activity = (FragmentActivity) mContext;
        AddProductFragment AddProductFragment = new AddProductFragment();
        Bundle arguments = new Bundle();
        arguments.putString("ProdID" , prodID);
        arguments.putString("CusID", userIdentity);
        AddProductFragment.setArguments(arguments);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, AddProductFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
