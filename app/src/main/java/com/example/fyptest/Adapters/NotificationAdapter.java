package com.example.fyptest.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.fyptest.database.notificationClass;
import com.example.fyptest.database.productClass;
import com.example.fyptest.database.productGroupClass;
import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.loginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {
    List<notificationClass> notificationItem;
    Context context;

    SharedPreferences preferences;
    String userIdentity;

    public NotificationAdapter(Context context, List<notificationClass> notificationItem) {
        this.notificationItem = notificationItem;
        this.context = context;

        //Identifying User
        try {
            this.preferences = context.getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = preferences.getString("userID", "UNKNOWN");
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            context.startActivity(new Intent(context, loginActivity.class));
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.notification_listing, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationAdapter.ImageViewHolder imageViewHolder, int i) {
        notificationClass uploadCurrent = notificationItem.get(i);
        final String prodID = uploadCurrent.getNoti_prodID();
        String title = uploadCurrent.getNoti_Title();
        String content = uploadCurrent.getNoti_Description();
        imageViewHolder.textViewTitle.setText(title);
        imageViewHolder.textViewContent.setText(content);

        if (title.equalsIgnoreCase("A group was created for an item in your Watchlist")) {
            imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupCustomAdapter.swapToProductView(context, prodID, userIdentity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notificationItem.size();
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
