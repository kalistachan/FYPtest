package com.example.fyptest;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.ProductListingFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class GroupCustomAdapter extends RecyclerView.Adapter<GroupCustomAdapter.ImageViewHolder> {
    Context mContext;
    List<productClass> groupList;

    SharedPreferences preferences;
    String userIdentity;

    public GroupCustomAdapter(Context applicationContext, List<productClass> groupList) {
        this.mContext = applicationContext;
        this.groupList = groupList;
        this.preferences = applicationContext.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.group_image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final GroupFragment gf = new GroupFragment();
        final productClass uploadCurrent = groupList.get(position);
        final String prodID = uploadCurrent.getPro_ID();
        long remainingDays = calculateRemainingTime(prodID, uploadCurrent.getPro_durationForGroupPurchase());
        final String prodName = uploadCurrent.getPro_name();
        final String targetQty = uploadCurrent.getPro_targetQuantity();
        final String timeRemain = String.valueOf(remainingDays);
        String minPrice = getMinPrice(uploadCurrent.getPro_retailPrice(), uploadCurrent.getPro_minOrderDiscount());

        holder.prodTextName.setText(prodName);
        holder.targetQty.setText(targetQty);
        holder.timeRemain.setText(timeRemain + " days left");
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItemFromRecycleView(position);
                gf.checkingConditionForRemoval(prodID, userIdentity, groupList, mContext);
            }
        });
    }

    private String getMinPrice(String retailPrice, String minDisc) {
        float fRetailPrice = Float.parseFloat(retailPrice);
        float value = 100;
        float fMinDisc = Float.parseFloat(minDisc) / value;
        float minSellPrice = fRetailPrice * fMinDisc;
        String floatToStringMinPrice = "S$" + Float.toString(minSellPrice);
        return floatToStringMinPrice;
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

    private void removeItemFromRecycleView(int position) {
        groupList.remove(position);
        notifyItemRemoved(position);
    }

    private long calculateRemainingTime(final String prodID, final String duration) {
        final long[] diffDays = new long[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Group");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("pg_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                        try {
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            Date dateCreated = sdf.parse(snapshot.child("string_pgDateCreated").getValue().toString());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(dateCreated);
                            cal.add(Calendar.DATE, Integer.parseInt(duration));
                            Date currentTime = Calendar.getInstance().getTime();
                            Date afterDurationDate = cal.getTime();
                            long diff =  afterDurationDate.getTime() - currentTime.getTime();
                            diffDays[0] = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                        } catch (java.text.ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return diffDays[0];
    }

}
