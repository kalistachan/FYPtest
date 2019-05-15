package com.example.fyptest.Admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.loginActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ImageViewHolder> {
    private Context mContext;
    private List<productClass> productList;

    private SharedPreferences preferences;
    private String userIdentity;


    AdminAdapter(Context applicationContext, List<productClass> productList) {
        this.mContext = applicationContext;
        this.productList = productList;

        //Identifying User
        try {
            this.preferences = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = preferences.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            mContext.startActivity(new Intent(mContext, loginActivity.class));
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.admin_product_listing, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {
        final productClass uploadCurrent = productList.get(i);
        final String productID = uploadCurrent.getPro_ID();
        final String prodName = uploadCurrent.getPro_name();
        final String prodDesc = uploadCurrent.getPro_description();
        final String prodPrice = "$" + uploadCurrent.getPro_maxOrderQtySellPrice();
        final String targetQty = "0/" + uploadCurrent.getPro_targetQuantity();
        final String duration = uploadCurrent.getPro_durationForGroupPurchase() + " days";
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(imageViewHolder.image_view_upload);

        imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToProductView(mContext,productID);
            }
        });

        imageViewHolder.textViewProductName.setText(prodName);
        imageViewHolder.textViewProductPrice.setText(prodPrice);
        imageViewHolder.textViewTargetQty.setText(targetQty);
        imageViewHolder.textViewDuration.setText(duration);
        imageViewHolder.textViewProductDescription.setText(prodDesc);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ImageViewHolder extends  RecyclerView.ViewHolder{
        ImageView image_view_upload;
        TextView textViewProductName, textViewProductPrice, textViewTargetQty, textViewDuration, textViewProductDescription;

        ImageViewHolder(View itemView) {
            super(itemView);
            this.image_view_upload = (ImageView) itemView.findViewById(R.id.image_view_upload);
            this.textViewProductName = (TextView) itemView.findViewById(R.id.textViewProductName);
            this.textViewProductPrice = (TextView) itemView.findViewById(R.id.textViewProductPrice);
            this.textViewTargetQty = (TextView) itemView.findViewById(R.id.textViewTargetQty);
            this.textViewDuration = (TextView) itemView.findViewById(R.id.textViewDuration);
            this.textViewProductDescription = (TextView) itemView.findViewById(R.id.textViewProductDescription);
        }
    }

    private void swapToProductView(Context mContext, String prodID) {
        Activity activity = (FragmentActivity) mContext;
        ManagePendingProduct AddProductFragment = new ManagePendingProduct();
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
