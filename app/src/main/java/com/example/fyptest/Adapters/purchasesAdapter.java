package com.example.fyptest.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
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
import com.example.fyptest.database.orderHistoryClass;
import com.example.fyptest.database.productClass;
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

public class purchasesAdapter extends RecyclerView.Adapter<purchasesAdapter.ImageViewHolder>{
    Context context;
    List<productClass> productList;
    List<orderHistoryClass> orderHistoryList;
    List<String> itemList;

    SharedPreferences preferences;
    String userIdentity;

    public purchasesAdapter(Context context, List<productClass> itemList) {
        this.context = context;
        this.productList = itemList;

        this.orderHistoryList = new ArrayList<>();
        this.itemList = new ArrayList<>();

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
    public int getItemCount() {
        return productList.size();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.purchases_listing, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder imageViewHolder, int i) {
        final productClass uploadCurrent = productList.get(i);
        final String prodName = uploadCurrent.getPro_name();
        final String prodID = uploadCurrent.getPro_ID();
        final String freeShippingCondition = uploadCurrent.getPro_freeShippingAt();
        final String shippingFee = uploadCurrent.getPro_shippingCost();
        imageViewHolder.prodNameViewName.setText(prodName);
        Picasso.get()
                .load(uploadCurrent.getPro_mImageUrl())
                .fit()
                .centerCrop()
                .into(imageViewHolder.image_view_upload);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Order History").child(userIdentity);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("oh_pro_ID").getValue().toString().equalsIgnoreCase(prodID)) {
                        final String orderHistoryID = snapshot.child("oh_ID").getValue().toString();
                        String productCheckoutPrice = snapshot.child("oh_orderedPrice").getValue().toString();
                        String quantityPurchase = snapshot.child("oh_totalQuantity").getValue().toString();
                        float oh_shippingFee = Float.parseFloat(snapshot.child("oh_shippingFee").getValue().toString());
                        float amountPaid = (Float.parseFloat(productCheckoutPrice) * Float.parseFloat(quantityPurchase)) + oh_shippingFee;
                        String intToStringForPrice = String.format("%.2f", amountPaid);

                        imageViewHolder.totalPaidPrice.setText("$" + intToStringForPrice);
                        imageViewHolder.viewShippingFeePrice.setText("$" + String.format("%.2f",(oh_shippingFee)));
                        imageViewHolder.prodPriceViewName.setText("$" + productCheckoutPrice);
                        imageViewHolder.quantity.setText("x" + quantityPurchase);
                        imageViewHolder.orderStatusTextView.setText(snapshot.child("oh_os").getValue().toString());

                        if (!snapshot.child("oh_os").getValue().toString().equalsIgnoreCase("Delivered")) {
                            imageViewHolder.lineBeforeBtn.setVisibility(View.GONE);
                            imageViewHolder.btnTrackOrder.setVisibility(View.GONE);
                        } else {
                            imageViewHolder.btnTrackOrder.setText("Acknowledge");
                            imageViewHolder.btnTrackOrder.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Order History").child(userIdentity).child(orderHistoryID).child("oh_os");
                                    String oh_os = "Completed";
                                    dbProduct.setValue(oh_os);
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Debug: onCancelled (dbWatchList)", databaseError.getMessage());
            }
        });
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image_view_upload;
        TextView prodNameViewName, quantity, prodPriceViewName, orderStatusTextView, viewShippingFeePrice, totalPaidPrice;
        Button btnTrackOrder;
        View lineBeforeBtn;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image_view_upload = itemView.findViewById(R.id.image_view_upload);
            prodNameViewName = itemView.findViewById(R.id.prodNameViewName);
            quantity = itemView.findViewById(R.id.quantity);
            prodPriceViewName = itemView.findViewById(R.id.prodPriceViewName);
            viewShippingFeePrice = itemView.findViewById(R.id.viewShippingFeePrice);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            totalPaidPrice = itemView.findViewById(R.id.totalPaidPrice);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
            lineBeforeBtn = itemView.findViewById(R.id.lineBeforeBtn);
        }
    }
}
