package com.example.fyptest.Seller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.MainActivity;
import com.example.fyptest.R;
import com.example.fyptest.Seller.MainFragmentAdapter;
import com.example.fyptest.Seller.fragment_main;
import com.example.fyptest.database.productClass;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.loginActivity;
import com.example.fyptest.registerActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class AddProductFragment extends Fragment {

    ImageView imgView;
    EditText editProductName, editProductPrice, duration, editTextProdDesc, maxDis,
            minTar, minDis, editTextShipCost, editTextFreeShipCondition, editTextTQ;
    Spinner productType;
    TextView textViewMaxPrice, textViewMinPrice, maxTar;
    CheckBox checkBoxFreeShipment;
    Button buttonAddProduct, buttonCancelProduct;

    DatabaseReference databaseProduct;
    FirebaseStorage storage;
    StorageReference storageReference;
    SharedPreferences prefs;

    String prodId, imageUrl, userIdentity, prodID;
    Bundle arguments;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        arguments = getArguments();

        //Identifying User
        try {
            this.prefs = getContext().getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = prefs.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(getContext(), loginActivity.class));
        }

        this.imgView = (ImageView) view.findViewById(R.id.imgView);
        this.productType = (Spinner) view.findViewById(R.id.productType);
        this.checkBoxFreeShipment = (CheckBox) view.findViewById(R.id.checkBoxFreeShipment);

        this.editProductName = (EditText) view.findViewById(R.id.editProductName);
        this.editProductPrice = (EditText) view.findViewById(R.id.editProductPrice);
        this.duration = (EditText) view.findViewById(R.id.duration);
        this.editTextProdDesc = (EditText) view.findViewById(R.id.editTextProdDesc);
        this.maxDis = (EditText) view.findViewById(R.id.maxDis);
        this.minTar = (EditText) view.findViewById(R.id.minTar);
        this.minDis = (EditText) view.findViewById(R.id.minDis);
        this.editTextShipCost = (EditText) view.findViewById(R.id.editTextShipCost);
        this.editTextFreeShipCondition = (EditText) view.findViewById(R.id.editTextFreeShipCondition);
        this.editTextTQ = (EditText) view.findViewById(R.id.editTextTQ);

        this.textViewMaxPrice = (TextView) view.findViewById(R.id.textViewMaxPrice);
        this.textViewMinPrice = (TextView) view.findViewById(R.id.textViewMinPrice);
        this.maxTar = (TextView) view.findViewById(R.id.maxTar);

        this.buttonAddProduct = (Button) view.findViewById(R.id.buttonAddProduct);
        this.buttonCancelProduct = (Button) view.findViewById(R.id.buttonCancelProduct);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filePath =  chooseImage();
            }
        });

        checkBoxFreeShipment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextFreeShipCondition.setEnabled(true);
                } else {
                    editTextFreeShipCondition.setEnabled(false);
                }
            }
        });

        if (arguments.getString("ProdID") != null) {
            final String productID = arguments.getString("ProdID");
            fillAddProdContent();

            buttonAddProduct.setText("Edit");
            buttonCancelProduct.setText("Remove");
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product Group").child(productID);
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        EditText[] editTexts = new EditText[]{editProductName, editProductPrice, duration, maxDis, minTar, minDis,
                                editTextShipCost, editTextFreeShipCondition, editTextTQ, editTextProdDesc};

                        for (EditText item : editTexts) {
                            item.setInputType(0);
                            item.setTextIsSelectable(true);
                            item.setFocusable(false);
                        }

                        checkBoxFreeShipment.setClickable(false);
                        imgView.setClickable(false);
                        productType.setEnabled(false);

                        buttonAddProduct.setEnabled(false);
                        buttonCancelProduct.setEnabled(false);
                    } else {
                        buttonAddProduct.setEnabled(true);
                        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String pro_name = "", pro_description = "", pro_retailPrice = "", pro_maxOrderQtySellPrice = textViewMaxPrice.getText().toString(),
                                        pro_minOrderQtySellPrice = textViewMinPrice.getText().toString(), pro_maxOrderDiscount = "", pro_minOrderAccepted = "", pro_minOrderDiscount = "",
                                        pro_shippingCost = "", pro_freeShippingAt = "", pro_durationForGroupPurchase = "", pro_Status = "pending", pro_aproveBy = null,
                                        pro_productType = productType.getSelectedItem().toString(), pro_s_ID = userIdentity, pro_targetQuantity = "";

                                if (checkNull(editProductName)) {pro_name = editProductName.getText().toString().trim();}

                                if (checkNull(editTextProdDesc)) {pro_description = editTextProdDesc.getText().toString().trim();}

                                if (checkNull(editProductPrice)) {pro_retailPrice = editProductPrice.getText().toString().trim();}

                                if (checkNull(duration)) {
                                    if (Integer.parseInt(duration.getText().toString()) < 4 || Integer.parseInt(duration.getText().toString()) > 30) {
                                        duration.setError("Between 4 to 30");
                                    } else {
                                        pro_durationForGroupPurchase = duration.getText().toString().trim();
                                    }
                                }

                                if (checkNull(maxDis)) {pro_maxOrderDiscount = maxDis.getText().toString().trim();}

                                if (checkNull(minTar)) {pro_minOrderAccepted = minTar.getText().toString().trim();}

                                if (checkNull(minDis)) {pro_minOrderDiscount = minDis.getText().toString().trim();}

                                if (checkNull(editTextShipCost)) {pro_shippingCost = editTextShipCost.getText().toString().trim();}

                                if (checkBoxFreeShipment.isChecked()) {
                                    if (checkNull(editTextFreeShipCondition)) {pro_freeShippingAt = editTextFreeShipCondition.getText().toString().trim();}
                                } else if (!checkBoxFreeShipment.isChecked()) {
                                    pro_freeShippingAt = null;
                                }

                                if (checkNull(editTextTQ)) { pro_targetQuantity = editTextTQ.getText().toString().trim();}

                                boolean result = validate(new String[] {pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice,
                                        pro_minOrderQtySellPrice, pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_durationForGroupPurchase,
                                        pro_Status, pro_productType, pro_s_ID, pro_targetQuantity});

                                if (result){
                                    addProd(pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount,
                                            pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy,
                                            pro_productType, pro_s_ID, pro_targetQuantity);
                                }
                            }
                        });

                        buttonCancelProduct.setEnabled(true);
                        buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MainFragmentAdapter.removeProduct(productID);
                                fragment_main fragment = new fragment_main();
                                Activity activity = (FragmentActivity) getContext();
                                FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        else if (arguments.getString("ProdID") == null) {
            buttonAddProduct.setText("Add");
            buttonCancelProduct.setText("Cancel");

            editTextFreeShipCondition.setEnabled(false);

            buttonAddProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pro_name = "", pro_description = "", pro_retailPrice = "", pro_maxOrderQtySellPrice = textViewMaxPrice.getText().toString(),
                            pro_minOrderQtySellPrice = textViewMinPrice.getText().toString(), pro_maxOrderDiscount = "", pro_minOrderAccepted = "", pro_minOrderDiscount = "",
                            pro_shippingCost = "", pro_freeShippingAt = "", pro_durationForGroupPurchase = "", pro_Status = "pending", pro_aproveBy = null,
                            pro_productType = productType.getSelectedItem().toString(), pro_s_ID = userIdentity, pro_targetQuantity = "";

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

                    boolean checkNull = false;
                    if (checkBoxFreeShipment.isChecked()) {
                        if (checkNull(editTextFreeShipCondition)) {
                            pro_freeShippingAt = editTextFreeShipCondition.getText().toString().trim();
                            checkNull = true;
                        }
                        else if (!checkNull(editTextFreeShipCondition)) {
                            checkNull = false;
                        }
                    } else if (!checkBoxFreeShipment.isChecked()) {
                        pro_freeShippingAt = null;
                    }

                    if (checkNull(editTextTQ)) { pro_targetQuantity = editTextTQ.getText().toString().trim();}

                    boolean result = validate(new String[] {pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice,
                            pro_minOrderQtySellPrice, pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_durationForGroupPurchase,
                            pro_Status, pro_productType, pro_s_ID, pro_targetQuantity});

                    if (checkBoxFreeShipment.isChecked()) {
                        if (result && checkNull) {
                            addProd(pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount,
                                    pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy,
                                    pro_productType, pro_s_ID, pro_targetQuantity);
                        }
                    } else if (!checkBoxFreeShipment.isChecked()) {
                        if (result) {
                            addProd(pro_name, pro_description, pro_retailPrice, pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount,
                                    pro_minOrderAccepted, pro_minOrderDiscount, pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy,
                                    pro_productType, pro_s_ID, pro_targetQuantity);
                        }
                    }

                }
            });

            buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Fragment fragment = new fragment_main();
                    transaction.replace(R.id.frame_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        //OnChangeListener to change MaxSellPrice & MinSellPrice as minDisc, maxDis & productPrice has change
        if (!editProductPrice.getText().toString().equals("0.00")) {
            maxDis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text1 = s.toString();
                    String text2 = editProductPrice.getText().toString();
                    if (!TextUtils.isEmpty(text1)) {
                        if (!TextUtils.isEmpty(text2)) {
                            float retailPrice = Float.parseFloat(text2);
                            float value = 100;
                            float maxDisc = Float.parseFloat(text1) / value;
                            float maxSellPrice = retailPrice - (retailPrice * maxDisc);
                            String floatToString = String.format("%.2f", maxSellPrice);
                            textViewMaxPrice.setText(floatToString);
                            String retailToString = String.format("%.2f", retailPrice);
                            editProductPrice.setText(retailToString);
                        } else {
                            editProductPrice.setHint("0.00");
                            textViewMaxPrice.setText("0.00");
                        }
                    } else {
                        textViewMaxPrice.setText("0.00");
                        maxDis.setHint("100%");
                    }
                }
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {}
            });
            minDis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text1 = s.toString();
                    String text2 = editProductPrice.getText().toString();
                    if (!TextUtils.isEmpty(text1)) {
                        if (!TextUtils.isEmpty(text2)) {
                            float retailPrice = Float.parseFloat(text2);
                            float value = 100;
                            float minDisc = Float.parseFloat(text1) / value;
                            float minSellPrice = retailPrice - retailPrice * minDisc;
                            String floatToString = String.format("%.2f", minSellPrice);
                            textViewMinPrice.setText(floatToString);
                            String retailToString = String.format("%.2f", retailPrice);
                            editProductPrice.setText(retailToString);
                        } else {
                            editProductPrice.setHint("0.00");
                            textViewMinPrice.setText("0.00");
                        }
                    } else {
                        textViewMinPrice.setText("0.00");
                        minDis.setHint("100%");
                    }
                }
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {}
            });
            editProductPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String retailPriceText = s.toString();
                    String maxDisText = maxDis.getText().toString();
                    String minDisText = minDis.getText().toString();
                    if (!TextUtils.isEmpty(retailPriceText)) {
                        if (!TextUtils.isEmpty(maxDisText)) {
                            float retailPrice = Float.parseFloat(retailPriceText);
                            float value = 100;
                            float maxDisc = Float.parseFloat(maxDisText) / value;
                            float maxSellPrice = retailPrice - retailPrice * maxDisc;
                            String floatToString = String.format("%.2f", maxSellPrice);
                            textViewMaxPrice.setText(floatToString);
                        } else {
                            textViewMaxPrice.setText("0.00");
                        }
                        if (!TextUtils.isEmpty(minDisText)) {
                            float retailPrice = Float.parseFloat(retailPriceText);
                            float value = 100;
                            float minDisc = Float.parseFloat(minDisText) / value;
                            float minSellPrice = retailPrice - retailPrice * minDisc;
                            String floatToString = String.format("%.2f", minSellPrice);
                            textViewMinPrice.setText(floatToString);
                        } else {
                            textViewMinPrice.setText("0.00");
                        }
                    } else {
                        textViewMaxPrice.setText("0.00");
                        textViewMinPrice.setText("0.00");
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void addProd (final String pro_name, final String pro_description, final String pro_retailPrice, final String pro_maxOrderQtySellPrice, final String pro_minOrderQtySellPrice,
                          final String pro_maxOrderDiscount, final String pro_minOrderAccepted, final String pro_minOrderDiscount, final String pro_shippingCost,
                          final String pro_freeShippingAt, final String pro_durationForGroupPurchase, final String pro_Status, final String pro_aproveBy, final String pro_productType,
                          final String pro_s_ID, final String pro_targetQuantity) {
        storage= FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseProduct = FirebaseDatabase.getInstance().getReference("Product");

        prodID = arguments.getString("ProdID");
        if (prodID == null) {
            prodId = databaseProduct.push().getKey();
            if(filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = storageReference.child("images/"+ prodId + "." + getFileExtension(filePath));
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

                                    productClass productClass = new productClass(prodId, imageUrl, pro_name, pro_description, pro_retailPrice,
                                            pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount,
                                            pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy, pro_productType, pro_s_ID, pro_targetQuantity);

                                    databaseProduct.child(prodId).setValue(productClass);

                                    //Redirecting user back to productListing Screen
                                    Fragment newFragment = new fragment_main();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.frame_container, newFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();

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
        else if (prodID != null) {
            if (filePath == null) {
                String imagePath = imageUrl;
                productClass productClass = new productClass(prodID, imagePath, pro_name, pro_description, pro_retailPrice,
                        pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount,
                        pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy, pro_productType, pro_s_ID, pro_targetQuantity);

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
                db.setValue(productClass);
                Toast.makeText(getContext(), "Product Successfully Updated", Toast.LENGTH_SHORT).show();
            }
            else if (filePath != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                //Remove Old filepath
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete();

                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                final StorageReference ref = storageReference.child("images/"+ prodID + "." + getFileExtension(filePath));
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

                                    productClass productClass = new productClass(prodID, imageUrl, pro_name, pro_description, pro_retailPrice,
                                            pro_maxOrderQtySellPrice, pro_minOrderQtySellPrice, pro_maxOrderDiscount, pro_minOrderAccepted, pro_minOrderDiscount,
                                            pro_shippingCost, pro_freeShippingAt, pro_durationForGroupPurchase, pro_Status, pro_aproveBy, pro_productType, pro_s_ID, pro_targetQuantity);

                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
                                    db.setValue(productClass);
                                    Toast.makeText(getContext(), "Product Successfully Updated", Toast.LENGTH_SHORT).show();
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
            }
        }
    }

    public void setSpinText(Spinner spin, String text) {
        for (int i = 0 ; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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

    private boolean validate(String[] text) {
        for (int i = 0; i < text.length; i++) {
            String currentText = text[i];
            if (currentText.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void fillAddProdContent() {
        prodID = arguments.getString("ProdID");
        DatabaseReference dbProduct = FirebaseDatabase.getInstance().getReference("Product").child(prodID);
        dbProduct.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pro_description = dataSnapshot.child("pro_description").getValue().toString();
                String pro_durationForGroupPurchase = dataSnapshot.child("pro_durationForGroupPurchase").getValue().toString();
                String pro_mImageUrl = dataSnapshot.child("pro_mImageUrl").getValue().toString();
                String pro_maxOrderDiscount = dataSnapshot.child("pro_maxOrderDiscount").getValue().toString();
                String pro_minOrderAccepted = dataSnapshot.child("pro_minOrderAccepted").getValue().toString();
                String pro_minOrderDiscount = dataSnapshot.child("pro_minOrderDiscount").getValue().toString();
                String pro_name = dataSnapshot.child("pro_name").getValue().toString();
                String pro_productType = dataSnapshot.child("pro_productType").getValue().toString();
                String pro_retailPrice = dataSnapshot.child("pro_retailPrice").getValue().toString();
                String pro_shippingCost = dataSnapshot.child("pro_shippingCost").getValue().toString();
                String pro_targetQuantity = dataSnapshot.child("pro_targetQuantity").getValue().toString();

                imageUrl = pro_mImageUrl;

                Picasso.get()
                        .load(pro_mImageUrl)
                        .into(imgView);
                editProductName.setText(pro_name);
                setSpinText(productType, pro_productType);
                duration.setText(pro_durationForGroupPurchase);
                editTextProdDesc.setText(pro_description);
                editTextTQ.setText(pro_targetQuantity);
                editProductPrice.setText(pro_retailPrice);
                maxDis.setText(pro_maxOrderDiscount);
                minTar.setText(pro_minOrderAccepted);
                minDis.setText(pro_minOrderDiscount);
                editTextShipCost.setText(pro_shippingCost);

                if (dataSnapshot.hasChild("pro_freeShippingAt")) {
                    String freeShipping = dataSnapshot.child("pro_freeShippingAt").getValue().toString();
                    checkBoxFreeShipment.setChecked(true);
                    editTextFreeShipCondition.setEnabled(true);
                    editTextFreeShipCondition.setText(freeShipping);
                } else if (!dataSnapshot.hasChild("pro_freeShippingAt")) {
                    checkBoxFreeShipment.setChecked(false);
                    editTextFreeShipCondition.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}