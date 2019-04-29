package com.example.fyptest.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.example.fyptest.fragments.GroupFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {
    List<productClass> itemList;
    Context context;

    SharedPreferences preferences;
    String userIdentity;

    public NotificationAdapter(Context context, List<productClass> productList) {
        this.itemList = productList;
        this.context = context;

        this.preferences = context.getSharedPreferences("IDs", MODE_PRIVATE);
        this.userIdentity = preferences.getString("userID", "UNKNOWN");
    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.notification_listing, viewGroup, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "1", Toast.LENGTH_LONG).show();
            }
        });
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder imageViewHolder, int i) {
        productClass uploadCurrent = itemList.get(i);
        final String pro_id = uploadCurrent.getPro_ID();
        final String pro_Name = uploadCurrent.getPro_name();
        String constructTitle = "There is a group created for the item " + pro_Name;
        String constructContent = "Click here to view information about " + pro_Name;
        imageViewHolder.textViewTitle.setText(constructTitle);
        imageViewHolder.textViewContent.setText(constructContent);

        imageViewHolder.textViewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupCustomAdapter.swapToProductView(context, pro_id, userIdentity);
            }
        });

        imageViewHolder.textViewContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupCustomAdapter.swapToProductView(context, pro_id, userIdentity);
            }
        });
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
