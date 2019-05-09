package com.example.fyptest;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.database.creditCardClass;
import com.example.fyptest.database.customerInfoClass;
import com.example.fyptest.database.userClass;
import com.example.fyptest.fragments.ProfileFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class registerActivity extends AppCompatActivity {

    private static final String TAG = "registerActivity";

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";

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

        //Save to User Class
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        inputPassword2 = (EditText) findViewById(R.id.inputPassword2);
        inputContactNo = (EditText) findViewById(R.id.inputContactNo);

        //Save to Customer Class
        inputFirstName = (EditText) findViewById(R.id.inputFirstName);
        inputLastName = (EditText) findViewById(R.id.inputLastName);
        inputShippingAddress = (EditText) findViewById(R.id.inputShippingAddress);
        inputPostalCode = (EditText) findViewById(R.id.inputPostalCode);

        //Save to Credit Card Class
        inputCCNum = (EditText) findViewById(R.id.inputCCNum);
        inputCVV = (EditText) findViewById(R.id.inputCVV);
        inputExpiryDate = (TextView) findViewById(R.id.inputExpiryDate);

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
                String cus_email = "", cus_password = "", cus_firstName = "", cus_LastName = "",
                        cus_address = "", cus_contactNum = "", cus_postalCode = "", ccnum = "", ccCVNum = "";

                if (checkNull(inputEmail)) {
                    if (isEmailValid(inputEmail)){
                        cus_email = inputEmail.getText().toString().trim();
                    }
                }

                if (checkLength(inputPassword,8,16) || checkLength(inputPassword2, 8, 16)) {
                    if (checkLength(inputPassword,8,16) && checkLength(inputPassword2, 8, 16)) {
                        if (confirmingPassword(inputPassword,inputPassword2)) {
                            cus_password = inputPassword.getText().toString().trim();
                        }
                    }
                }

                if (isPhoneNumValid(inputContactNo)) {
                    cus_contactNum = inputContactNo.getText().toString().trim();
                }

                if (checkNull(inputFirstName)) {
                    cus_firstName = inputFirstName.getText().toString().trim();
                }

                if (checkNull(inputLastName)) {
                    cus_LastName = inputLastName.getText().toString().trim();
                }

                if (checkNull(inputShippingAddress)) {
                    cus_address = inputShippingAddress.getText().toString().trim();
                }

                if (checkLength(inputPostalCode,6,6)) {
                    cus_postalCode = inputPostalCode.getText().toString();
                }

//                if (checkLength(inputCCNum,16,16)) {
//                    ccnum = inputCCNum.getText().toString().trim();
//                    if (ProfileFragment.isCCValid(Long.parseLong(ccnum))) {
//                        ccnum = inputCCNum.getText().toString().trim();
//                    } else {
//                        inputCCNum.setError("Invalid credit card number");
//                    }
//                }

                if (ProfileFragment.isCCValid(Long.parseLong(inputCCNum.getText().toString().trim()))) {
                    ccnum = inputCCNum.getText().toString().trim();
                } else {
                    inputCCNum.setError("Invalid credit card number");
                }

                if (checkLength(inputCVV,3,3)) {
                    ccCVNum = inputCVV.getText().toString().trim();
                }

                boolean result = validate(new String[] {cus_email, cus_password, cus_firstName, cus_LastName, cus_address,
                        cus_contactNum, cus_postalCode, ccnum, ccCVNum});

                if (result) {
                    DatabaseReference dbCustomer, dbUser, dbCreditCard;
                    dbCustomer = FirebaseDatabase.getInstance().getReference("Customer Information");
                    dbUser = FirebaseDatabase.getInstance().getReference("User");
                    dbCreditCard = FirebaseDatabase.getInstance().getReference("Credit Card Detail");

                    //generating unique ID for customer and credit card
                    String userID = dbUser.push().getKey();
                    String ccID = dbCreditCard.push().getKey();

                    //creating new loyalty point for customer
                    int loyaltyPoint = 0;

                    //Getting expiry date
                    String expiry = inputExpiryDate.getText().toString();

                    //Constructing elements using data class file
                    userClass userClass = null;
                    creditCardClass creditCardClass = null;
                    try {
                        userClass = new userClass(userID, cus_email, encrypt(cus_password), cus_contactNum, "customer");
                        creditCardClass = new creditCardClass(ccID, encrypt(ccnum), expiry, encrypt(ccCVNum), userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    customerInfoClass customerInfoClass = new customerInfoClass(userID, cus_firstName, cus_LastName, cus_address, cus_postalCode, loyaltyPoint, "customer");


                    //Adding value into database
                    dbUser.child(userID).setValue(userClass);
                    dbCustomer.child(userID).setValue(customerInfoClass);
                    dbCreditCard.child(userID).setValue(creditCardClass);

                    //Clear Form
                    clearForm((ViewGroup) findViewById(R.id.cus_register));
                    inputExpiryDate.setText("Select Expiry Date");

                    //Redirect user back to login screen
                    startActivity(new Intent(registerActivity.this, loginActivity.class).putExtra("IntentSource", "accCreated"));

                } if (!result) {
                    Toast.makeText(registerActivity.this, "Check Field", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private static boolean checkLength(EditText editText, int minLength, int maxLength) {
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

    private boolean validate(String[] text) {
        for (int i = 0; i < text.length; i++) {
            String currentText = text[i];
            if (currentText.length() <= 0 || currentText == "" || currentText == null) {
                return false;
            }
        }
        return true;
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

    public static String encrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(registerActivity.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    public static String decrypt(String value) throws Exception
    {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(registerActivity.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        String decryptedValue = new String(decryptedByteValue,"utf-8");
        return decryptedValue;

    }

    private static Key generateKey() throws Exception
    {
        Key key = new SecretKeySpec(registerActivity.KEY.getBytes(),registerActivity.ALGORITHM);
        return key;
    }

}
