package com.example.fyptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fyptest.database.Product;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Product> {
    Context context;
    List<Product> productList;
    LayoutInflater inflter;
    int resource;

    public CustomAdapter(Context applicationContext, int resource, List<Product> productList) {
        super(applicationContext, resource, productList);
        this.context = applicationContext;
        this.resource = resource;
        this.productList = productList;
      //  inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflter = LayoutInflater.from(context);
        view = inflter.inflate(R.layout.fragment_group, null);
        TextView prodName = (TextView) view.findViewById(R.id.prodNameViewName);
        TextView prodPrice = (TextView) view.findViewById(R.id.prodPriceViewName);
        //ImageView icon = (ImageView) view.findViewById(R.id.icon);
        Product product = productList.get(i);

        prodName.setText(product.getProdName());
        prodPrice.setText(product.getProdPrice());

        return view;
    }
}
