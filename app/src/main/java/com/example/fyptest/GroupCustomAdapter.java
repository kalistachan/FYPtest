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
        final String prodName = uploadCurrent.getPro_name();
        final String targetQty = uploadCurrent.getPro_targetQuantity();
        String minPrice = getMinPrice(uploadCurrent.getPro_retailPrice(), uploadCurrent.getPro_minOrderDiscount());
        holder.prodPriceView.setText(minPrice);
        holder.prodTextName.setText(prodName);
        holder.targetQty.setText(targetQty);
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() == 1) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                    String groupDetailID = snapshot.child("gd_ID").getValue().toString();
                                    db.child(groupDetailID).removeValue();
                                    DatabaseReference dbAgain = FirebaseDatabase.getInstance().getReference("Product Group").child(prodID);
                                    dbAgain.removeValue();
                                    return;
                                }
                            }
                        } else if (dataSnapshot.getChildrenCount() > 1) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                    String groupDetailID = snapshot.child("gd_ID").getValue().toString();
                                    db.child(groupDetailID).removeValue();
                                    return;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Debug: onCancelled (dbProduct)", databaseError.getMessage());
                    }
                });
            }
        });

        readData(new FirebaseCallback() {

            @Override
            public void onCallback1(final String timeRemain) {
                if (timeRemain != null) {
                    holder.timeRemain.setText(timeRemain + " days left");
                }
            }
        },prodID, uploadCurrent.getPro_durationForGroupPurchase());
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
        TextView prodPriceView;
        Button leaveBtn;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            prodTextName = itemView.findViewById(R.id.prodNameViewName);
            targetQty = itemView.findViewById(R.id.targetQty);
            timeRemain = itemView.findViewById(R.id.timeRemain);
            imageView = itemView.findViewById(R.id.image_view_upload);
            prodPriceView = itemView.findViewById(R.id.prodPriceViewName);
            leaveBtn = itemView.findViewById(R.id.groupbtn1);

        }
    }

    private void removeItemFromRecycleView(int position, List<productClass> list) {
        if (position > -1) {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            notifyDataSetChanged();
        }

    }

    private void readData (final GroupCustomAdapter.FirebaseCallback firebaseCallback, final String prodID, final String duration) {
        final String[] diffDays = new String[1];
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
                                long diff = afterDurationDate.getTime() - currentTime.getTime();
                                long remainingDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                                diffDays[0] = String.valueOf(remainingDays);

                            } catch (java.text.ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    firebaseCallback.onCallback1(diffDays[0]);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }

    private interface FirebaseCallback {
        void onCallback1(String days);
    }

}
