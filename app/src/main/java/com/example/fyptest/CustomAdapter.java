package com.example.fyptest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        public TextView prodTextName;
        public TextView prodTextPrice;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            prodTextName = itemView.findViewById(R.id.prodNameViewName);
            prodTextPrice = itemView.findViewById(R.id.prodPriceViewName);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }

    /*
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflter = LayoutInflater.from(mContext);
        view = inflter.inflate(R.layout.fragment_group, null);
        TextView prodName = (TextView) view.findViewById(R.id.prodNameViewName);
        TextView prodPrice = (TextView) view.findViewById(R.id.prodPriceViewName);
        //ImageView icon = (ImageView) view.findViewById(R.id.icon);
        Product product = productList.get(i);

        prodName.setText(product.getProdName());
        prodPrice.setText(product.getProdPrice());

        return view;
    }*/
}
