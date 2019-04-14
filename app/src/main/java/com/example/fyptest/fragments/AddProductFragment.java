package com.example.fyptest.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.Product;
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

public class AddProductFragment extends Fragment {

    TextView textView;
    EditText editProductName, editProductPrice;
    Spinner spinner;
    Button buttonAddProduct;

    ImageView imgView;
    DatabaseReference databaseProduct;
    FirebaseStorage storage;
    StorageReference storageReference;
    String prodId;
    String imageUrl;

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

        editProductName = (EditText) getView().findViewById(R.id.editProductName);
        editProductPrice = (EditText) getView().findViewById(R.id.editProductPrice);
        buttonAddProduct = (Button) getView().findViewById(R.id.buttonAddProduct);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath =  chooseImage();
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct(editProductName,editProductPrice, filePath);
            }
        });

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

}