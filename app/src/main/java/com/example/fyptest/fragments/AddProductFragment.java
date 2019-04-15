package com.example.fyptest.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.Product;
import com.example.fyptest.database.productClass;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class AddProductFragment extends Fragment {

    ImageView imgView;
    EditText editProductName, editProductPrice, duration, editTextProdDesc, maxDis,
            minTar, minDis, editTextShipCost, editTextFreeShipCondition;
    Spinner productType;
    TextView textViewMaxPrice, textViewMinPrice, maxTar, dayOrWeek;
    CheckBox checkBoxFreeShipment;
    Button buttonAddProduct, buttonCancelProduct;

    DatabaseReference databaseProduct;
    FirebaseStorage storage;
    StorageReference storageReference;
    String prodId;
    String imageUrl;

    SharedPreferences prefs;
    String userIdentity;


    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgView = (ImageView) getView().findViewById(R.id.imgView);

        productType = (Spinner) getView().findViewById(R.id.productType);

        editProductName = (EditText) getView().findViewById(R.id.editProductName);
        editProductPrice = (EditText) getView().findViewById(R.id.editProductPrice);
        duration = (EditText) getView().findViewById(R.id.duration);
        editTextProdDesc = (EditText) getView().findViewById(R.id.editTextProdDesc);
        maxDis = (EditText) getView().findViewById(R.id.maxDis);
        minTar = (EditText) getView().findViewById(R.id.minTar);
        minDis = (EditText) getView().findViewById(R.id.minDis);
        editTextShipCost = (EditText) getView().findViewById(R.id.editTextShipCost);
        editTextFreeShipCondition = (EditText) getView().findViewById(R.id.editTextFreeShipCondition);

        textViewMaxPrice = (TextView) getView().findViewById(R.id.textViewMaxPrice);
        textViewMinPrice = (TextView) getView().findViewById(R.id.textViewMinPrice);
        maxTar = (TextView) getView().findViewById(R.id.maxTar);
        dayOrWeek = (TextView) getView().findViewById(R.id.dayOrWeek);

        checkBoxFreeShipment = (CheckBox) getView().findViewById(R.id.checkBoxFreeShipment);

        buttonAddProduct = (Button) getView().findViewById(R.id.buttonAddProduct);
        buttonCancelProduct = (Button) getView().findViewById(R.id.buttonCancelProduct);

        prefs = getContext().getSharedPreferences("IDs", MODE_PRIVATE);
        userIdentity = prefs.getString("userID", null);

        if (!editProductPrice.getText().toString().equals("Retail Price")) {
            maxDis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
//                    String ori = maxDis.getText().toString() + "%";
//                    maxDis.setText(ori);
//                    float originalPrice = Float.parseFloat(editProductPrice.getText().toString());
                    float discount = (Float.parseFloat(s.toString()) / 100);
//                    float finalValue = originalPrice * discount;
                    textViewMaxPrice.setText(Float.toString(discount));
                }
            });
            minDis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
//                    String ori = minDis.getText().toString() + "%";
//                    minDis.setText(ori);
//                    float originalPrice = Float.parseFloat(editProductPrice.getText().toString());
                    float discount = (Float.parseFloat(s.toString()) / 100);
//                    float finalValue = originalPrice * discount;
                    textViewMinPrice.setText(Float.toString(discount));
                }
            });
        }

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath =  chooseImage();
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pro_mImageUrl = "", pro_name = "", pro_description = "", pro_retailPrice = "", pro_maxOrderQtySellPrice = textViewMaxPrice.getText().toString().trim(),
                        pro_minOrderQtySellPrice = textViewMinPrice.getText().toString().trim(), pro_maxOrderDiscount = "", pro_minOrderAccepted = "", pro_minOrderDiscount = "",
                        pro_shippingCost = "", pro_freeShippingAt = "", pro_durationForGroupPurchase = "", pro_Status = "pending", pro_aproveBy = null,
                        pro_productType = productType.toString().trim(), pro_s_ID = userIdentity;

                if (checkNull(editProductName)) {pro_name = editProductName.getText().toString().trim();}

                if (checkNull(editTextProdDesc)) {pro_description = editTextProdDesc.getText().toString().trim();}

                if (checkNull(editProductPrice)) {pro_retailPrice = editProductPrice.getText().toString().trim();}

                if (checkNull(duration)) {
                    if (Integer.parseInt(duration.getText().toString()) < 4 || Integer.parseInt(duration.getText().toString()) > 30) {
                        duration.setError("Invalid Duration");
                    } else {
                        pro_durationForGroupPurchase = duration.getText().toString().trim();
                    }
                }

                if (checkNull(maxDis)) {pro_maxOrderDiscount = maxDis.getText().toString().trim();}

                if (checkNull(minTar)) {pro_minOrderAccepted = minTar.getText().toString().trim();}

                if (checkNull(minDis)) {pro_minOrderDiscount = minDis.getText().toString().trim();}

                if (checkNull(editTextShipCost)) {pro_shippingCost = editTextShipCost.getText().toString().trim();}

                if (checkBoxFreeShipment.isChecked()) {
                    if (checkNull(editTextFreeShipCondition)) {}
                } else { pro_freeShippingAt = null;}


//                String[] list = new String[]{pro_mImageUrl, pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice,
//                        pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase,
//                        pro_Status, pro_aproveBy, pro_productType, pro_s_ID};




                //addProduct(editProductName,editProductPrice, filePath);
            }
        });

        buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void addProd (String[] list) {

    }

    public void addProduct (EditText prodName, EditText prodPrice, Uri filePath) {
        final String prodNameText = prodName.getText().toString().trim();
        final String prodPriceText = prodPrice.getText().toString().trim();

        if (!TextUtils.isEmpty(prodNameText)) {
            if (!TextUtils.isEmpty(prodPriceText)) {
                if(filePath != null) {
                    uploadImage(prodNameText, prodPriceText);
                    prodName.setText("");
                    prodPrice.setText("");
                    imgView.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_placeholder));
                }  else { Toast.makeText(getActivity().getApplicationContext(), "Please choose a product image", Toast.LENGTH_LONG).show(); }
            } else { prodPrice.setError("Empty Field"); }
        } else { prodName.setError("Empty Field"); }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage(final String prodNameText, final String prodPriceText) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");
        prodId = databaseProduct.push().getKey();

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ prodId + "." + getFileExtension(filePath));
            //StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            final UploadTask uploadTask = ref.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                imageUrl = task.getResult().toString();

                                Product product = new Product(prodId, prodNameText, prodPriceText, imageUrl);

                                databaseProduct.child(prodId).setValue(product);
                                Toast.makeText(getContext(), "Product Successfully Added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                }
            });
        } else {
            Toast.makeText(getContext(), "No image chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        if (filePath != null) {
            return filePath;
        } else {
            return null;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imgView.setImageBitmap(bitmap);
                Log.d("IMAGE: ", "image exist " + bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean checkNull(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Field Required");
            return false;
        } else {
            return true;
        }
    }

}