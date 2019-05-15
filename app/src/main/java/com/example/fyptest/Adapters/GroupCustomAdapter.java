package com.example.fyptest.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.ProductView;
import com.example.fyptest.loginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class GroupCustomAdapter extends RecyclerView.Adapter<GroupCustomAdapter.ImageViewHolder> {
    private Context mContext;
    private List<productClass> groupList;
    private SharedPreferences preferences;
    private String userIdentity;
    private DatabaseReference dbGroupDetail;

    public GroupCustomAdapter(Context applicationContext, List<productClass> groupList) {
        this.mContext = applicationContext;
        this.groupList = groupList;

        //Identifying User
        try {
            this.preferences = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = preferences.getString("userID", "UNKNOWN");
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            mContext.startActivity(new Intent(mContext, loginActivity.class));
        }
    }

    @Override
    public int getItemCount() {
        return groupList.size();
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
        final String targetQty = uploadCurrent.getPro_targetQuantity();
        String prodRetail = "$" + uploadCurrent.getPro_retailPrice();
        String minPrice = getMinPrice(uploadCurrent.getPro_retailPrice(), uploadCurrent.getPro_minOrderDiscount());
        holder.prodPriceView.setText(minPrice);
        holder.prodTextName.setText(prodName);
        holder.prodRetailPrice.setText(prodRetail);
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
        dbGroupDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int counter = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                }
                String buildText = Integer.toString(counter) + " / " + targetQty;
                holder.targetQty.setText(buildText);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ProductView productView = new ProductView();
        productView.checkConditionForLeavingGroup(holder.leaveBtn, prodID);

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
                                    removeItemFromRecycleView(position, groupList);
                                    return;
                                }
                            }
                        } else if (dataSnapshot.getChildrenCount() > 1) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                    String groupDetailID = snapshot.child("gd_ID").getValue().toString();
                                    db.child(groupDetailID).removeValue();
                                    removeItemFromRecycleView(position, groupList);
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
                    if (Integer.parseInt(timeRemain) > 0) {
                        holder.timeRemain.setText(timeRemain + " days left");
                    } else if (Integer.parseInt(timeRemain) == 0){
                        //Checkout & Remove from group database
                    }
                }
            }
        },prodID, uploadCurrent.getPro_durationForGroupPurchase());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToProductView(mContext,prodID, userIdentity);
            }
        });
    }

    private String getMinPrice(String retailPrice, String minDisc) {
        float fRetailPrice = Float.parseFloat(retailPrice);
        float value = 100;
        float fMinDisc = Float.parseFloat(minDisc) / value;
        float minSellPrice = fRetailPrice - (fRetailPrice * fMinDisc);
        String floatToStringMinPrice = "$" + String.format("%.2f",(minSellPrice));
        return floatToStringMinPrice;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView prodTextName;
        TextView targetQty;
        TextView timeRemain;
        TextView prodRetailPrice;
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
            prodRetailPrice = itemView.findViewById(R.id.prodRetail);

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

    private void readData (final FirebaseCallback firebaseCallback, final String prodID, final String duration) {
        final String[] diffDays = new String[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Group");
        db.addValueEventListener(new ValueEventListener() {
            @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("pg_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                Date dateCreated = sdf.parse(snapshot.child("pg_dateCreated").getValue().toString());
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

    public static void swapToProductView(Context mContext, String prodID, String userIdentity) {
        Activity activity = (FragmentActivity) mContext;
        ProductView newProductView = new ProductView();
        Bundle arguments = new Bundle();
        arguments.putString("ProdID" , prodID);
        arguments.putString("CusID", userIdentity);
        newProductView.setArguments(arguments);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newProductView);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
