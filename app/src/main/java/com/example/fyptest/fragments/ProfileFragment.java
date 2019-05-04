package com.example.fyptest.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.loginActivity;
import com.example.fyptest.registerActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.fyptest.R.id.cus_register;


public class ProfileFragment extends Fragment {

    SharedPreferences pref;
    String getStr;

    DatabaseReference dbUser, dbCusInfo, dbCC;

    EditText email, contactNo, Password, confirmPassword, address, ccExpiryDate, ccNum, ccCVV, postalCode;

    TextView profileTitle1;

    Switch notification;

    Button update, logout;

    List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pref = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        getStr = pref.getString("userID", null);

        dbUser = FirebaseDatabase.getInstance().getReference("User").child(getStr);
        dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr);
        dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(getStr);

        email = (EditText) view.findViewById(R.id.email);
        contactNo = (EditText) view.findViewById(R.id.contactNo);
        Password = (EditText) view.findViewById(R.id.Password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        address = (EditText) view.findViewById(R.id.address);
        postalCode = (EditText) view.findViewById(R.id.postalCode);
        ccExpiryDate = (EditText) view.findViewById(R.id.ccExpiryDate);
        ccNum = (EditText) view.findViewById(R.id.ccNum);
        ccCVV = (EditText) view.findViewById(R.id.ccCVV);

        notification = (Switch) view.findViewById(R.id.switch1);
        update = (Button) view.findViewById(R.id.updateBtn);
        logout = (Button) view.findViewById(R.id.logoutBtn);

        profileTitle1 = (TextView) view.findViewById(R.id.profileTitle1);

        notification.setText(notification.getTextOn());

        list = new ArrayList<>();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getEmail = email.getText().toString(), getContactNo = contactNo.getText().toString(),
                        getPassword = Password.getText().toString(), getConfirmPassword = confirmPassword.getText().toString(),
                        getAddress = address.getText().toString(), getPostalCode = postalCode.getText().toString(),
                        getCCExpiryDate = ccExpiryDate.getText().toString(), getCCNum = ccNum.getText().toString(),
                        getCCCVV = ccCVV.getText().toString();

                if (!getEmail.isEmpty()) {
                    if (isEmailValid(email)) {
                        dbUser = FirebaseDatabase.getInstance().getReference("User").child(getStr).child("email");
                        dbUser.setValue(getEmail);
                    }
                }

                if (!getContactNo.isEmpty()) {
                    if (isPhoneNumValid(contactNo)) {
                        dbUser = FirebaseDatabase.getInstance().getReference("User").child(getStr).child("contactNum");
                        dbUser.setValue(getContactNo);
                    }
                }

                if (!getPassword.isEmpty() && !getConfirmPassword.isEmpty()) {
                    if (checkLength(Password, 8 , 16) || checkLength(confirmPassword, 8 , 16)) {
                        if (checkLength(Password, 8 , 16) && checkLength(confirmPassword, 8 , 16)) {
                            if (confirmingPassword(Password, confirmPassword)) {
                                dbUser = FirebaseDatabase.getInstance().getReference("User").child(getStr).child("password");
                                dbUser.setValue(getPassword);
                            }
                        }
                    }
                }

                if (!getAddress.isEmpty()) {
                    dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr).child("cus_shippingAddress");
                    dbCusInfo.setValue(getAddress);
                }

                if (!getPostalCode.isEmpty()) {
                    if (checkLength(postalCode,6,6)) {
                        dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr).child("cus_postalCode");
                        dbCusInfo.setValue(getPostalCode);
                    }
                }

                if (!getCCExpiryDate.isEmpty()) {
                    dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(getStr).child("cc_ExpiryDate");
                    dbCC.setValue(getCCExpiryDate);
                }

                if (!getCCNum.isEmpty()) {
                    if (checkLength(ccNum,16,16)) {
                        registerActivity ra = new registerActivity();
                        if (ra.isCCValid(Long.parseLong(getCCNum)) == false) {
                            ccNum.setError("Invalid credit card number");
                        } else {
                            dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(getStr).child("cc_Num");
                            dbCC.setValue(getCCNum);
                        }
                    }
                }

                if (!getCCCVV.isEmpty()) {
                    if (checkLength(ccCVV,3,3)) {
                        dbCC = FirebaseDatabase.getInstance().getReference("Credit Card Detail").child(getStr).child("cc_CVNum");
                        dbCC.setValue(getCCCVV);
                    }
                }

                if (notification.isChecked()) {
                    dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr).child("cus_Notification");
                    String update = notification.getTextOn().toString();
                    dbCusInfo.setValue(update);
                } else {
                    dbCusInfo = FirebaseDatabase.getInstance().getReference("Customer Information").child(getStr).child("cus_Notification");
                    String update = notification.getTextOff().toString();
                    dbCusInfo.setValue(update);
                }

                clearForm((ViewGroup) view.findViewById(R.id.profileForm));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.apply();
                startActivity(new Intent(getActivity(), loginActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email.setHint(dataSnapshot.child("email").getValue().toString());
                list.add("email");
                contactNo.setHint(dataSnapshot.child("contactNum").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCusInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fName = dataSnapshot.child("cus_fName").getValue().toString();
                String lName = dataSnapshot.child("cus_lName").getValue().toString();
                profileTitle1.setText(lName + " " + fName + " Information");
                String add = dataSnapshot.child("cus_shippingAddress").getValue().toString();
                String postal = dataSnapshot.child("cus_postalCode").getValue().toString();
                address.setHint(add);
                postalCode.setHint(" Singapore " + postal);

                if (dataSnapshot.child("cus_Notification").getValue().toString().equalsIgnoreCase("Enable")) {
                    notification.setChecked(true);
                } else if (dataSnapshot.child("cus_Notification").getValue().toString().equalsIgnoreCase("Disable")) {
                    notification.setChecked(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });

        dbCC.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ccNum.setHint(dataSnapshot.child("cc_Num").getValue().toString().substring(0, 6) + "xxxxxx" + dataSnapshot.child("cc_Num").getValue().toString().substring(12, 16));
                ccExpiryDate.setHint(dataSnapshot.child("cc_ExpiryDate").getValue().toString());
                ccCVV.setHint(dataSnapshot.child("cc_CVNum").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
    }

    private static boolean checkLength(EditText editText, int minLength, int maxLength) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Invalid Inputs");
            return false;
        } if (text.length() < minLength) {
            editText.setError("Invalid Inputs");
            return false;
        } if (text.length() > maxLength) {
            editText.setError("Invalid Inputs");
            return false; }

        return true;

    }

    private static boolean confirmingPassword(EditText password, EditText confirmPassword) {
        String mainText = password.getText().toString().trim();
        String confirmationText = confirmPassword.getText().toString().trim();
        if (confirmationText.equals(mainText)) {
            return true;
        } else {
            password.setError("Password does not match");
            confirmPassword.setError("Password does not match");
            return false;
        }
    }

    private static boolean isEmailValid(EditText editText) {
        String email = editText.getText().toString();
        String expression1 = "^[a-zA-Z0-9_]+@[hotmail]+\\.+[com, sg]+$";
        String expression2 = "^[a-zA-Z0-9_]+@[email, gmail, outlook]+\\.+[com]+$";
        Pattern pattern1 = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(expression2, Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(email);
        Matcher matcher2 = pattern2.matcher(email);

        if (matcher1.matches()) {
            return true;
        } if (matcher2.matches()) {
            return true;
        } if (!matcher1.matches() && !matcher2.matches()) {
            editText.setError("Invalid Input");
            return false;
        } else {return true;}
    }

    private static boolean isPhoneNumValid(EditText editText){
        String phoneNum = editText.getText().toString().trim();
        String expression = "^[8,9]{1,1}+[0-9]{7,7}+$";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNum);
        if (matcher.matches()) {
            return true;
        } else {
            editText.setError("Invalid Input");
            return false;
        }
    }

    private void clearForm(ViewGroup group) {
        int count = group.getChildCount();
        for (int i = 0; i < count; i ++) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }
            if (view instanceof ViewGroup && ((ViewGroup)view).getChildCount() > 0) {
                clearForm((ViewGroup)view);
            }
        }
    }
}
