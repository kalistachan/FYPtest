package com.example.fyptest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.ProductListingFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GroupCustomAdapter extends RecyclerView.Adapter<GroupCustomAdapter.ImageViewHolder> {
    Context mContext;
    List<productClass> groupList;


    public GroupCustomAdapter(Context applicationContext, List<productClass> groupList) {
        this.mContext = applicationContext;
        this.groupList = groupList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.group_image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final productClass uploadCurrent = groupList.get(position);
        final String prodID = uploadCurrent.getPro_ID();
        final String prodName = uploadCurrent.getPro_name();
        holder.prodTextName.setText(prodName);
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView prodTextName;
        TextView targetQty;
        TextView timeRemain;
        ImageView imageView;
        Button leaveBtn;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            prodTextName = itemView.findViewById(R.id.prodNameViewName);
            targetQty = itemView.findViewById(R.id.targetQty);
            timeRemain = itemView.findViewById(R.id.timeRemain);
            imageView = itemView.findViewById(R.id.image_view_upload);
            leaveBtn = itemView.findViewById(R.id.groupbtn1);

        }
    }
}
