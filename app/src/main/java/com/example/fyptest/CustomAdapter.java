package com.example.fyptest;

import android.content.Context;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {
    Context mContext;
    List<Product> productList;
    ArrayList<String> itemList;
    boolean[] value;

    public CustomAdapter(Context applicationContext,  List<Product> productList) {
        this.mContext = applicationContext;
        this.productList = productList;
        this.itemList = new ArrayList<>();
        this.value = new boolean[1];
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final Product uploadCurrent = productList.get(position);
        final String prodID = uploadCurrent.getProdID();
        final String prodName = uploadCurrent.getProdName();
        final ProductListingFragment pl = new ProductListingFragment();
        holder.prodTextName.setText(prodName);
        holder.prodTextPrice.setText(uploadCurrent.getProdPrice());
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<String> list) {
                if (list.isEmpty()) {
                    holder.grpBtn.setText("Create Group");
                    holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pl.ShowDialog(mContext, prodID, prodName);
                        }
                    });
                } else {
                    for (String item : list) {
                        if (item.equalsIgnoreCase(prodID)) {
                            holder.grpBtn.setText("Join Group");
                            break;
                        } else {
                            holder.grpBtn.setText("Create Group");
                            holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pl.ShowDialog(mContext, prodID, prodName);
                                }
                            });
                        }
                    }
                }

            }
        });
    }

    private void readData (final FirebaseCallback firebaseCallback) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Group");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productID = snapshot.child("pg_pro_ID").getValue().toString();
                    itemList.add(productID);
                }
                firebaseCallback.onCallback(itemList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(List<String> list);
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

        }
    }

}
