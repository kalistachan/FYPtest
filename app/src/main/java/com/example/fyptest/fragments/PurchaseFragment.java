package com.example.fyptest.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;


public class PurchaseFragment extends Fragment {
    EditText editProductName;
    EditText editProductPrice;
    DatabaseReference databaseProduct;
    Button btnChoose, btnUpload, btnAddProduct, btnTest;
    ImageView imageView;


    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    public PurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        editProductName = (EditText) getView().findViewById(R.id.editProductName);
        editProductPrice = (EditText) getView().findViewById(R.id.editProductPrice);
        btnChoose = (Button) getView().findViewById(R.id.btnChoose);
        btnUpload = (Button) getView().findViewById(R.id.btnUpload);
        imageView = (ImageView) getView().findViewById(R.id.imgView);
        btnAddProduct = (Button) getView().findViewById(R.id.buttonAddProduct);

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addProduct(editProductName,editProductPrice);
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadImage();
            }
        });

    }

    public void addProduct (EditText prodName, EditText prodPrice) {
        databaseProduct = FirebaseDatabase.getInstance().getReference("product");

        String prodNameText = prodName.getText().toString().trim();
        prodName.setText("");

        String prodPriceText = prodPrice.getText().toString().trim();
        prodPrice.setText("");

        if (!TextUtils.isEmpty(prodNameText)) {
            String id = databaseProduct.push().getKey();

            Product product = new Product(id, prodNameText, prodPriceText);

            databaseProduct.child(id).setValue(product);

              Toast.makeText(getActivity().getApplicationContext(), "Product Added", Toast.LENGTH_LONG).show();
        } else {
             Toast.makeText(getActivity().getApplicationContext(),"You should enter a name", Toast.LENGTH_LONG).show();
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                Log.d("IMAGE: ", "image exist " + bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}