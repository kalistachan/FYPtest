package com.example.fyptest.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.watchlistClass;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.ProductView;
import com.example.fyptest.fragments.WatchListFragment;
import com.example.fyptest.loginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {
    private Context mContext;
    private List<productClass> productList;
    private ArrayList<String> itemList;
    private SharedPreferences pref;
    private String userIdentity;

    private DatabaseReference dbGroupDetail;

    ProductListingFragment pl;

    boolean[] value;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView prodTextName;
        TextView prodTextPrice;
        TextView prodTextRetail;
        TextView targetQty;
        TextView timeRemain;
        ImageView imageView;
        Button grpBtn;
        Button watchBtn;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            this.prodTextName = itemView.findViewById(R.id.prodNameViewName);
            this.prodTextPrice = itemView.findViewById(R.id.prodPriceViewName);
            this.prodTextRetail = itemView.findViewById(R.id.prodRetail);
            this.targetQty = itemView.findViewById(R.id.targetQty);
            this.timeRemain = itemView.findViewById(R.id.timeRemain);
            this.imageView = itemView.findViewById(R.id.image_view_upload);
            this.grpBtn = itemView.findViewById(R.id.btn1);
            this.watchBtn = itemView.findViewById(R.id.btn2);
        }
    }

    public CustomAdapter(Context applicationContext,  List<productClass> productList) {
        this.mContext = applicationContext;
        this.productList = productList;

        this.itemList = new ArrayList<>();
        this.value = new boolean[1];

        //Identifying User
        try {
            this.pref = mContext.getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = pref.getString("userID", "UNKNOWN");
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
        }

        this.pl = new ProductListingFragment();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final productClass uploadCurrent = productList.get(position);
        final String prodID = uploadCurrent.getPro_ID();
        final String prodName = uploadCurrent.getPro_name();
        holder.prodTextName.setText(prodName);
        holder.prodTextPrice.setText("$" + (uploadCurrent.getPro_maxOrderQtySellPrice()));
        holder.prodTextRetail.setText("$" + (uploadCurrent.getPro_retailPrice()));

        holder.timeRemain.setText(uploadCurrent.getPro_durationForGroupPurchase() + " days left");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToProductView(mContext,prodID);
            }
        });
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail").child(prodID);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    int counter = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        counter = counter + Integer.parseInt(snapshot.child("gd_qty").getValue().toString());
                    }
                    String text = counter + " / " + uploadCurrent.getPro_targetQuantity();
                    holder.targetQty.setText(text);
                } else {
                    String text = 0 + " / " + uploadCurrent.getPro_targetQuantity();
                    holder.targetQty.setText(text);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(List<String> itemList) {
                if (!itemList.isEmpty()) {
                    for (String item : itemList) {
                        if (item.equalsIgnoreCase(prodID)) {
                            dbGroupDetail = FirebaseDatabase.getInstance().getReference("Group Detail").child(item);
                            dbGroupDetail.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                            holder.grpBtn.setText("View Group");
                                            removeFromWatchList(prodID, userIdentity);
                                            holder.watchBtn.setVisibility(View.INVISIBLE);
                                            holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    pl.swapToGroupFragment(mContext);
                                                }
                                            });
                                            break;
                                        } else {
                                            pl.checkBlacklistedCard(holder.grpBtn, userIdentity);
                                            setToCreateOrJoinGroup(holder.grpBtn, prodID, prodName , "Join Group", 1, userIdentity, mContext);
                                            changeWatchButton(holder.watchBtn, userIdentity, prodID, mContext);
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(mContext, "Database Error: Error Code 401", Toast.LENGTH_LONG).show();
                                    Log.d("401", databaseError.getMessage());
                                }
                            });
                            break;
                        } else {
                            pl.checkBlacklistedCard(holder.grpBtn, userIdentity);
                            setToCreateOrJoinGroup(holder.grpBtn, prodID, prodName , "Create Group", 2, userIdentity, mContext);
                            changeWatchButton(holder.watchBtn, userIdentity, prodID, mContext);
                        }
                    }
                } else if (itemList.isEmpty()) {
                    pl.checkBlacklistedCard(holder.grpBtn, userIdentity);
                    setToCreateOrJoinGroup(holder.grpBtn, prodID, prodName , "Create Group", 2, userIdentity, mContext);
                    changeWatchButton(holder.watchBtn, userIdentity, prodID, mContext);
                }
                itemList.clear();
            }
        });
    }

    private void changeWatchButton(final Button button, final String userID, final String prodID, final Context context) {
        DatabaseReference dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
        dbWatchList.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    if (dataSnapshot.child(userID).hasChild(prodID)) {
                        if (dataSnapshot.child(userID).child(prodID).child("wl_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                            setButtonToViewWatchList(button, context);
                        } else if (!dataSnapshot.child(userID).child(prodID).child("wl_cus_ID").getValue().toString().equalsIgnoreCase(userID)) {
                            setButtonToAddWatchList(prodID, button, userID, context);
                        }
                    } else if (!dataSnapshot.child(userID).hasChild(prodID)) {
                        setButtonToAddWatchList(prodID, button, userID, context);
                    }
                } else if (!dataSnapshot.hasChild(userID)) {
                    setButtonToAddWatchList(prodID, button, userID, context);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){}
        });
    }

    private void readData (final FirebaseCallback firebaseCallback) {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private interface FirebaseCallback {
        void onCallback1(List<String> itemList);
    }

    public int getItemCount() {
        return productList.size();
    }

    private void swapToWatchListGragment (Context mContext) {
        Activity activity = (FragmentActivity) mContext;
        WatchListFragment newGroupFragment = new WatchListFragment();
        Log.d("activity ", "value: " + activity);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void addToWatchList(String prodID, Button button, String gdCusID, Context context) {
        DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("Watch List").child(gdCusID);
        String wl_ID = newDB.push().getKey();
        watchlistClass watchlistClass = new watchlistClass(wl_ID, gdCusID, prodID);
        newDB.child(prodID).setValue(watchlistClass);
        setButtonToViewWatchList(button, context);
    }

    private void removeFromWatchList(String prodID, String gdCusID) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Watch List").child(gdCusID).child(prodID);
        db.removeValue();

    }

    private void setButtonToAddWatchList(final String prodID, final Button button, final String gdCusID, final Context context) {
        button.setText(" Add To Watch List ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWatchList(prodID, button, gdCusID, context);
            }
        });
    }

    private void setButtonToViewWatchList(Button button, final Context context) {
        button.setText(" View Watch List ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToWatchListGragment(context);
            }
        });
    }

    private void setToCreateOrJoinGroup(final Button button, final String prodID, final String prodName , String btnName, final int option, final String gdCusID, final Context context) {
        button.setText(btnName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pl.ShowDialog(context, prodID, prodName, button, option, gdCusID);
            }
        });
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
