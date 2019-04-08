package com.example.fyptest;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.fyptest.database.creditCardClass;
import com.example.fyptest.database.customerClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.net.IDN;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class registerActivity extends AppCompatActivity {

    private static final String TAG = "registerActivity";

    EditText inputEmail, inputPassword, inputPassword2, inputFirstName, inputLastName,
            inputContactNo, inputShippingAddress, inputPostalCode, inputCCNum,
            inputCVV;

    TextView inputExpiryDate;

    Button bottomSubmit, bottomBack2Login;

    DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final String selectedDate = "";

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputPassword2 = (EditText) findViewById(R.id.inputPassword2);
        inputFirstName = (EditText) findViewById(R.id.inputFirstName);
        inputLastName = (EditText) findViewById(R.id.inputLastName);
        inputContactNo = (EditText) findViewById(R.id.inputContactNo);
        inputShippingAddress = (EditText) findViewById(R.id.inputShippingAddress);
        inputPostalCode = (EditText) findViewById(R.id.inputPostalCode);
        inputCCNum = (EditText) findViewById(R.id.inputCCNum);
        inputExpiryDate = (TextView) findViewById(R.id.inputExpiryDate);
        inputCVV = (EditText) findViewById(R.id.inputCVV);

        bottomSubmit = (Button) findViewById(R.id.bottomSubmit);
        bottomBack2Login = (Button) findViewById(R.id.bottomBack2Login);

        inputExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        registerActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d(TAG,"onDateSet: mm/dd/yyyy: " + month + "/" + dayOfMonth + "/" + year);

                String date = month + "/" + year;
                inputExpiryDate.setText(date);
            }
        };

        bottomBack2Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(registerActivity.this, loginActivity.class));
            }
        });

        bottomSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check1 = false; boolean check2 = false; boolean check3 = false;
                boolean check4 = false; boolean check5 = false; boolean check6 = false;
                boolean check7 = false; boolean check8 = false; boolean check9 = false;

                if (checkNull(inputEmail)) { if (isEmailValid(inputEmail)) { check1 = true; }}

                if (checkLength(inputPassword,8,16) || checkLength(inputPassword2, 8, 16)) {
                    if (checkLength(inputPassword,8,16) && checkLength(inputPassword2, 8, 16)) {
                        if (confirmingPassword(inputPassword,inputPassword2)) {
                            check2 = true;
                        }
                    }
                }

                if (checkNull(inputFirstName)) { check3 = true; }

                if (checkNull(inputLastName)) { check4 = true; }

                if (checkNull(inputShippingAddress)) { check5 = true; }

                if (!isPhoneNumValid(inputContactNo)) { check6 = true; }

                if (checkLength(inputPostalCode,6,6)) { check7 = true; }

                if (checkLength(inputCCNum,16,16)) { check8 = true; }

                if (checkLength(inputCVV,3,3)) { check9 = true; }

                DatabaseReference dbUserType, dbUser, dbCreditCard;
                dbUserType = FirebaseDatabase.getInstance().getReference();
                dbUser = FirebaseDatabase.getInstance().getReference("User");
                dbCreditCard = FirebaseDatabase.getInstance().getReference("Credit Card Detail");

                String userID = dbUser.push().getKey();
                String ccID = dbCreditCard.push().getKey();

                //Information for personal particular
                String cus_email = inputEmail.getText().toString().trim();
                String cus_contactNum = inputContactNo.getText().toString().trim();
                String cus_firstName = inputFirstName.getText().toString().trim();
                String cus_LastName = inputLastName.getText().toString().trim();
                String cus_password = inputPassword.getText().toString().trim();
                String cus_address = inputShippingAddress.getText().toString().trim();
                String cus_postalCode = inputPostalCode.getText().toString();
                int loyaltyPoint = 0;

                customerClass customerClass = new customerClass(userID, cus_email, cus_contactNum, cus_firstName,
                        cus_LastName, cus_password, cus_address, cus_postalCode,
                        loyaltyPoint, "customer");
                dbUser.child(userID).setValue(customerClass);

                //Information for credit card
                String ccnum = inputCCNum.getText().toString().trim();
                String ccCVNum = inputCVV.getText().toString().trim();
                String expiry = inputExpiryDate.getText().toString();

                creditCardClass creditCardClass = new creditCardClass(ccID, ccnum, expiry, ccCVNum, userID);
                dbCreditCard.child(userID).setValue(creditCardClass);
                
            }
        });

    }

    public static boolean checkLength(EditText editText, int minLength, int maxLength) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Field Required");
            return false;
        } if (text.length() < minLength) {
            editText.setError("Invalid Inputs");
            return false;
        } if (text.length() > maxLength) {
            editText.setError("Invalid Inputs");
            return false; }

        return true;

    }

    public static boolean checkNull(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Field Required");
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkTextView(TextView textView) {
        String text = textView.getText().toString().trim();
        textView.setError(null);

        if (text.length() == 0) {
            textView.setError("Field Required");
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPhoneNumValid(EditText editText){
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

    public static boolean isEmailValid(EditText editText) {
        String email = editText.getText().toString();
        String expression1 = "^[a-zA-Z0-9_]{3}+@[hotmail]+\\.+[com, sg]+$";
        String expression2 = "^[a-zA-Z0-9_]{3}+@[email, gmail, outlook]+\\.+[com]+$";
        Pattern pattern1 = Pattern.compile(expression1, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(expression2, Pattern.CASE_INSENSITIVE);
        Matcher matcher1 = pattern1.matcher(email);
        Matcher matcher2 = pattern2.matcher(email);

        if (matcher1.matches() || matcher2.matches()) {
            return true;
        } else {
            editText.setError("Invalid Input");
            return false;
        }
    }

    public static boolean confirmingPassword(EditText password, EditText confirmPassword) {
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

    public void addCustomer(final EditText email, final EditText contactNum, final EditText firstName, final EditText LastName,
                            final EditText password, final EditText confirmPassword, final EditText address,
                            final EditText postalCode) {
        DatabaseReference databaseUserType = FirebaseDatabase.getInstance().getReference("User Type");
        databaseUserType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("ut_name").getValue().toString().equals("customer")) {
                        String ut_ID = snapshot.child("ut_ID").getValue().toString();
                        String ut_name = snapshot.child("ut_name").getValue().toString();
                        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference("User").child(ut_name);

                        String cus_email = email.getText().toString().trim();
                        String cus_contactNum = contactNum.getText().toString().trim();


                        if (checkNull(firstName) || checkNull(LastName) || checkLength(password, 8, 16)
                                || checkLength(confirmPassword, 8, 16) || checkNull(address) || checkLength(postalCode,6,6)) {
                            if (checkNull(firstName) && checkNull(LastName) && checkLength(password, 8, 16) &&
                                    checkLength(confirmPassword, 8, 16) && checkNull(address)
                                    && isPhoneNumValid(contactNum) && isEmailValid(email) && checkLength(postalCode,6,6)) {
                                if (confirmingPassword(password, confirmPassword)) {
                                    String cus_firstName = firstName.getText().toString().trim();
                                    String cus_LastName = LastName.getText().toString().trim();
                                    String cus_password = password.getText().toString().trim();
                                    String cus_address = address.getText().toString().trim();
                                    String cus_gender = postalCode.getText().toString();
                                    int cus_loyaltyPoint = 0;
                                    String foreignKey = ut_ID;

                                    String id = databaseUser.push().getKey();

                                    //Add Credit Card

                                    addCreditCard(inputCCNum, inputCVV, inputExpiryDate, cus_LastName + " " + cus_firstName, id);

                                    //Add Customer
                                    customerClass customerClass = new customerClass(id, cus_email, cus_contactNum,
                                            cus_firstName, cus_LastName, cus_password,
                                            cus_address, cus_gender, cus_loyaltyPoint, foreignKey);
                                    databaseUser.child(ut_name).setValue(customerClass);
                                }
                            }
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    public void addCreditCard(EditText cc_num, EditText cc_CVM, TextView expiryDate, String cusName, String cusID) {
        DatabaseReference databaseCreditCard = FirebaseDatabase.getInstance().getReference("Credit Card Detail");
        if (checkLength(cc_num, 16, 16) || checkLength(cc_CVM, 3, 3) || checkTextView(expiryDate)) {
            if (checkLength(cc_num, 16, 16) && checkLength(cc_CVM, 3, 3) && checkTextView(expiryDate)) {

                String id = databaseCreditCard.push().getKey();
                String ccnum = cc_num.getText().toString().trim();
                String ccCVNum = cc_CVM.getText().toString().trim();
                String expire = expiryDate.getText().toString();

                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/YY");
                String date = dateFormat.format(new Date());

                creditCardClass creditCardClass = new creditCardClass(id, ccCVNum, expire, ccnum, cusName);
                databaseCreditCard.child(cusID).setValue(creditCardClass);
            } else {}
        } else {}
    }
}
