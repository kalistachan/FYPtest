package com.example.fyptest.Admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.fyptest.R;
import com.example.fyptest.Seller.fragment_main;
import com.example.fyptest.database.sellerInfoClass;
import com.example.fyptest.database.userClass;
import com.example.fyptest.resetPWActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddSellerFragment extends Fragment {
    EditText editTextEmail, editTextSellerName, editTextContact;
    Button createBtn;

    public AddSellerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_seller, container, false);
        this.editTextEmail = (EditText) view.findViewById(R.id.editTextEmail);
        this.editTextSellerName = (EditText) view.findViewById(R.id.editTextSellerName);
        this.editTextContact = (EditText) view.findViewById(R.id.editTextContact);
        this.createBtn = (Button) view.findViewById(R.id.button6);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "", sellerName = "", contact = "";

                if (checkNull(editTextEmail)) {
                    if (isEmailValid(editTextEmail)) {
                        email = editTextEmail.getText().toString().trim();
                    }
                }

                if (checkNull(editTextSellerName)) {
                    sellerName = editTextSellerName.getText().toString().trim();
                }

                if (isPhoneNumValid(editTextContact)) {
                    contact = editTextContact.getText().toString().trim();
                }

                boolean result = validate(new String[] {email, sellerName, contact});

                if (result) {
                    final String checkMail = email, checkName = sellerName, checkContact = contact;
                    DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User");

                    userDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean email = false, contact = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("email").getValue().toString().equalsIgnoreCase(checkMail)) {
                                    email = true;
                                    break;
                                } else if (snapshot.child("contactNum").getValue().toString().equalsIgnoreCase(checkContact)) {
                                    contact = true;
                                    break;
                                }
                            }
                            if (!email && !contact){
                                addSeller(checkMail, checkName, checkContact);

                                Fragment newFragment = new AdminMainFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            } else if (email) {
                                editTextEmail.setError("Email is already in use. Try again.");
                            } else if (contact) {
                                editTextContact.setError("Contact number is already in use. Try again.");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public void addSeller(String addEmail, String addName, String addContact) {
        DatabaseReference userDB, sellerInfoDB;
        userDB = FirebaseDatabase.getInstance().getReference("User");
        sellerInfoDB = FirebaseDatabase.getInstance().getReference("Seller Information");

        String userID = sellerInfoDB.push().getKey();
        String pw = autoGeneratePassword(8);

        userClass userClass = new userClass(userID, addEmail, pw, addContact, "seller");
        sellerInfoClass sellerInfoClass = new sellerInfoClass(userID, addName, "seller");

        userDB.child(userID).setValue(userClass);
        sellerInfoDB.child(userID).setValue(sellerInfoClass);

        String emailSubject = "A new password for your 4GB account had been generated";
        String emailBody = "Your new password is";
        resetPWActivity.sendMail(addEmail, pw, emailSubject, emailBody);
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

    private boolean validate(String[] text) {
        for (String currentText : text) {
            if (currentText.length() <= 0) {
                return false;
            }
        }
        return true;
    }

    public static String autoGeneratePassword(int passwordLength) {
        char[] chars = "qwer1tyui2opQW3ERTY4UIOPas5dfgh6jklASD7FGHJK8Lzxcv9bnmZ0XCVBNM@!&".toCharArray();
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < passwordLength; i++) {
            char getChar = chars[random.nextInt(chars.length)];
            password.append(getChar);
        }

        return password.toString();
    }
}
