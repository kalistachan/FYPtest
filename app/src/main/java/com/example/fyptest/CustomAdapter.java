package com.example.fyptest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
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

import com.example.fyptest.database.productClass;
import com.example.fyptest.database.watchlistClass;
import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.WatchListFragment;
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
    Context mContext;
    List<productClass> productList;
    ArrayList<String> itemList;
    SharedPreferences pref;
    String userIdentity;

    DatabaseReference dbWatchList;

    boolean[] value;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView prodTextName;
        TextView prodTextPrice;
        ImageView imageView;
        Button grpBtn;
        Button watchBtn;

        public ImageViewHolder(final View itemView) {
            super(itemView);

            this.prodTextName = itemView.findViewById(R.id.prodNameViewName);
            this.prodTextPrice = itemView.findViewById(R.id.prodPriceViewName);
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
        this.pref = applicationContext.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = pref.getString("userID", "UNKNOWN");
        this.dbWatchList = FirebaseDatabase.getInstance().getReference("Watch List");
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
        final ProductListingFragment pl = new ProductListingFragment();
        holder.prodTextName.setText(prodName);
        holder.prodTextPrice.setText("$" + uploadCurrent.getPro_retailPrice());
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        //Amending with buttons & takes appropriate action
        readData(new FirebaseCallback() {
            @Override
            public void onCallback1(final List<String> list1) {
                if (list1.isEmpty()) {
                    holder.grpBtn.setText("Create Group");
                    holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pl.ShowDialog(mContext, prodID, prodName);
                        }
                    });
                    dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(userIdentity)) {
                                for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                                    if (snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                                        setButtonToViewWatchList(holder.watchBtn);
                                        break;
                                    } else if (!snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)){
                                        holder.watchBtn.setText(" Add to Watch List ");
                                        holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addToWatchList(prodID, userIdentity);
                                                setButtonToViewWatchList(holder.watchBtn);
                                            }
                                        });
                                    }
                                }
                            } else {
                                holder.watchBtn.setText(" Add to Watch List ");
                                holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addToWatchList(prodID, userIdentity);
                                        setButtonToViewWatchList(holder.watchBtn);
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    for (final String item : list1) {
                        if (item.equalsIgnoreCase(prodID)) {
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Group Detail");
                                db.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(prodID)) {
                                            for (DataSnapshot snapshot : dataSnapshot.child(prodID).getChildren()) {
                                                if (snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                                    dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild(userIdentity)) {
                                                                for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                                                                    if (snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                                                                        removeFromWatchList();
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    });
                                                    holder.grpBtn.setText("View Group");
                                                    holder.watchBtn.setVisibility(View.GONE);
                                                    holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            swapToGroupFragment(mContext);
                                                        }
                                                    });
                                                } else if (!snapshot.child("gd_cus_ID").getValue().toString().equalsIgnoreCase(userIdentity)) {
                                                    dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild(userIdentity)) {
                                                                for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                                                                    if (snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                                                                        setButtonToViewWatchList(holder.watchBtn);
                                                                        break;
                                                                    } else if (!snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)){
                                                                        holder.watchBtn.setText(" Add to Watch List ");
                                                                        holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                addToWatchList(prodID, userIdentity);
                                                                                setButtonToViewWatchList(holder.watchBtn);
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            } else {
                                                                holder.watchBtn.setText(" Add to Watch List ");
                                                                holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        addToWatchList(prodID, userIdentity);
                                                                        setButtonToViewWatchList(holder.watchBtn);
                                                                    }
                                                                });
                                                            }
                                                        }
                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        }
                                                    });
                                                    holder.grpBtn.setText("Join Group");
                                                    holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            pl.ShowDialog(mContext, prodID, prodName);
                                                            holder.watchBtn.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                        } else if (!item.equalsIgnoreCase(prodID)) {
                            dbWatchList.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(userIdentity)) {
                                        for (DataSnapshot snapshot : dataSnapshot.child(userIdentity).getChildren()) {
                                            if (snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                                                setButtonToViewWatchList(holder.watchBtn);
                                                break;
                                            } else if (!snapshot.child("wl_pro_ID").getValue().toString().equalsIgnoreCase(prodID)){
                                                holder.watchBtn.setText(" Add to Watch List ");
                                                holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        addToWatchList(prodID, userIdentity);
                                                        setButtonToViewWatchList(holder.watchBtn);
                                                    }
                                                });
                                            }
                                        }
                                    } else {
                                        holder.watchBtn.setText(" Add to Watch List ");
                                        holder.watchBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                addToWatchList(prodID, userIdentity);
                                                setButtonToViewWatchList(holder.watchBtn);
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                            holder.grpBtn.setText("Create Group");
                            holder.grpBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pl.ShowDialog(mContext, prodID, prodName);
                                    holder.watchBtn.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void readData (final FirebaseCallback firebaseCallback) {
        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference("Product Group");
        db1.addValueEventListener(new ValueEventListener() {
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

    private void setButtonToViewWatchList(Button button) {
        button.setText(" View Watch List ");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapToWatchListGragment(mContext);
            }
        });
    }

    public int getItemCount() {
        return productList.size();
    }

    private void swapToGroupFragment (Context mContext) {
        Activity activity = (FragmentActivity) mContext;
        GroupFragment newGroupFragment = new GroupFragment();
        Log.d("activity ", "value: " + activity);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newGroupFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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

    private void addToWatchList(String prodID, String userID) {
        DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("Watch List").child(userID);
        String wl_ID = dbWatchList.push().getKey();
        watchlistClass watchlistClass = new watchlistClass(wl_ID, userIdentity, prodID);
        newDB.child(wl_ID).setValue(watchlistClass);
    }

    private void removeFromWatchList() {

    }
}
