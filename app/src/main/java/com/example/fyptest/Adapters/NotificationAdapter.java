package com.example.fyptest.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {
    List<String> itemList;
    Context context;

    SharedPreferences preferences;
    String userIdentity;

    public NotificationAdapter(Context context, List<String> productList) {
        this.itemList = productList;
        this.context = context;
        this.preferences = context.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
    }

    @NonNull
    @Override
    public NotificationAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ImageViewHolder imageViewHolder, int i) {
        Log.d("12345", itemList.get(i));
//        for (String item : itemList) {
//            DatabaseReference dbProductGroup = FirebaseDatabase.getInstance().getReference("Product Group");
//            dbProductGroup.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.hasChild("item"));
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewContent;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            textViewContent = (TextView) itemView.findViewById(R.id.textViewContent);
        }
    }
}
