package com.example.fyptest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.ProductView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class watchListAdapter extends RecyclerView.Adapter<watchListAdapter.ImageViewHolder> {
    Context context;
    List<productClass> productList;
    List<String> itemList;

    SharedPreferences preferences;
    String userIdentity;

    CustomAdapter ca;
    ProductListingFragment pl;

    public watchListAdapter(Context context, List<productClass> productList) {
        this.productList = productList;
        this.context = context;
        this.preferences = context.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
        this.pl = new ProductListingFragment();
        this.itemList = new ArrayList<>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.watchlist_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, final int position) {
        final productClass uploadCurrent = productList.get(position);
        final String prodID = uploadCurrent.getPro_ID();
        final String prodName = uploadCurrent.getPro_name();
        final String retailPrice = uploadCurrent.getPro_retailPrice();
        final String minOrderQtySellPrice = uploadCurrent.getPro_minOrderQtySellPrice();
        final String maxOrderQtySellPrice = uploadCurrent.getPro_maxOrderQtySellPrice();
        final String shippingFee = uploadCurrent.getPro_shippingCost();
        final String freeShipping = uploadCurrent.getPro_freeShippingAt();
        final String targetQuantity = uploadCurrent.getPro_targetQuantity();
        final String timeRemain = uploadCurrent.getPro_durationForGroupPurchase();
        String minPrice = getMinPrice(uploadCurrent.getPro_retailPrice(), uploadCurrent.getPro_minOrderDiscount());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToProductView(context,prodID);
            }
        });
        viewHolder.prodNameViewName.setText(prodName);
        viewHolder.prodRetail.setText("$" + retailPrice);
        viewHolder.prodPriceViewName.setText("$" + minPrice);
        viewHolder.timeRemain.setText((timeRemain) + " days left");
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(viewHolder.image_view_upload);

        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(List<String> itemList) {
                DatabaseReference gpDetails = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
                gpDetails.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                        }
                        String buildText = Integer.toString(counter) + " / " + targetQuantity;

                        viewHolder.targetQty.setText(buildText);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if (!itemList.isEmpty()) {
                    for (String producdID : itemList) {
                        if (producdID.equalsIgnoreCase(prodID)) {

                            pl.checkBlacklistedCard(viewHolder.btnAdd, userIdentity);
                            setToCreateOrJoinGroup(viewHolder.btnAdd, prodID, prodName , "Join Group", 1, userIdentity, context, position);
                            break;
                        } else if (!producdID.equalsIgnoreCase(prodID)) {
                            pl.checkBlacklistedCard(viewHolder.btnAdd, userIdentity);
                            setToCreateOrJoinGroup(viewHolder.btnAdd, prodID, prodName , "Create Group", 2, userIdentity, context, position);
                        }
                    }
                } else {
                    pl.checkBlacklistedCard(viewHolder.btnAdd, userIdentity);
                    setToCreateOrJoinGroup(viewHolder.btnAdd, prodID, prodName , "Create Group", 2, userIdentity, context, position);
                }
            }
        });

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

    private interface FirebaseCallback {
        void onCallback1(List<String> itemList);
    }

    private String getMinPrice(String retailPrice, String minDisc) {
        float fRetailPrice = Float.parseFloat(retailPrice);
        float value = 100;
        float fMinDisc = Float.parseFloat(minDisc) / value;
        float minSellPrice = fRetailPrice - (fRetailPrice * fMinDisc);
        String floatToStringMinPrice = String.format("%.2f",(minSellPrice));
        return floatToStringMinPrice;
    }

    private void readData (final watchListAdapter.FirebaseCallback firebaseCallback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Group");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productID = snapshot.child("pg_pro_ID").getValue().toString();
                    itemList.add(productID);
                }
                firebaseCallback.onCallback1(itemList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setToCreateOrJoinGroup(final Button button, final String prodID, final String prodName , String btnName, final int option, final String gdCusID, final Context context, final int position) {
        button.setText(btnName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pl.ShowDialog(context, prodID, prodName, button, option, gdCusID);
            }
        });
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
        TextView prodNameViewName, prodPriceViewName, targetQty, timeRemain, prodRetail;
        Button btnRemove, btnAdd;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            image_view_upload = itemView.findViewById(R.id.image_view_upload);
            prodNameViewName = itemView.findViewById(R.id.prodNameViewName);
            prodPriceViewName = itemView.findViewById(R.id.prodPriceViewName);
            targetQty = itemView.findViewById(R.id.targetQty);
            prodRetail = itemView.findViewById(R.id.prodRetail);
            timeRemain = itemView.findViewById(R.id.timeRemain);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }

    public void swapToProductView(Context mContext, String prodID) {
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