package com.example.fyptest;

import android.content.Context;
import android.content.DialogInterface;
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
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ImageViewHolder> {
    Context mContext;
    List<Product> productList;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext,  List<Product> productList) {
        this.mContext = applicationContext;
        this.productList = productList;
      //  inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_listing, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Product uploadCurrent = productList.get(position);
        holder.prodTextName.setText(uploadCurrent.getProdName());
        holder.prodTextPrice.setText(uploadCurrent.getProdPrice());
        Picasso.get()
                .load(uploadCurrent.getImageUrl())
                .fit()
                .centerCrop()
                .into(holder.imageView);
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

        public ImageViewHolder(View itemView) {
            super(itemView);

            prodTextName = itemView.findViewById(R.id.prodNameViewName);
            prodTextPrice = itemView.findViewById(R.id.prodPriceViewName);
            imageView = itemView.findViewById(R.id.image_view_upload);
            grpBtn = itemView.findViewById(R.id.btn1);
            watchBtn = itemView.findViewById(R.id.btn2);

            String checkGroup ="exist";
            if (checkGroup == "exist") {
                grpBtn.setText("Join Group");
                grpBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       ShowDialog();
                    }
                });
            } else {
                grpBtn.setText("Create Group");
            }

            String checkWatch ="exist";
            if (checkWatch == "exist") {
                watchBtn.setText("In Watchlist");
                watchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } else {
                watchBtn.setText("Add to Watchlist");
            }
        }

        public void ShowDialog() {
            final AlertDialog.Builder popDialog = new AlertDialog.Builder(mContext);
            final SeekBar seek = new SeekBar(mContext);

            seek.setMax(100);
            popDialog.setIcon(android.R.drawable.btn_star_big_on);

            popDialog.setTitle("Please Select Rank 1-100 ");

            popDialog.setView(seek);

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                    //Do something here with new value
                    Log.d("Slider value: ", " " + progress);
                    //txtView.setText("Value of : " + progress);
                }

                public void onStartTrackingTouch(SeekBar arg0) {
                    // TODO Auto-generated method stub
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }
            });

            popDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            popDialog.create();
            popDialog.show();
        }
    }
}
