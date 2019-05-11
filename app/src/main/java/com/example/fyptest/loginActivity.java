package com.example.fyptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.Admin.AdminMainActivity;
import com.example.fyptest.Seller.SellerMainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.security.MessageDigest;

public class loginActivity extends AppCompatActivity implements Serializable {

    EditText emailEntered, passwordEntered;
    Button buttonLogin, buttonRegister;
    TextView forgotPassword;

    SharedPreferences prefs;
    Toast toast;

    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEntered = (EditText) findViewById(R.id.editTextEmail);
        passwordEntered = (EditText) findViewById(R.id.editTextPassword);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        prefs = getSharedPreferences("IDs", MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();

        if (getIntent().getSerializableExtra("IntentSource") != null) {
            String result = getIntent().getSerializableExtra("IntentSource").toString();
            if (result.equalsIgnoreCase("pwReset")) {
                Toast.makeText(loginActivity.this, "Your password has been reset, a new password will be send to your email", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("accCreated")) {
                Toast.makeText(loginActivity.this, "Account successfully created!", Toast.LENGTH_LONG).show();
            }
        }

        counter = 4;

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, resetPWActivity.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNull(emailEntered) || checkNull(passwordEntered)) {
                    if (checkNull(emailEntered) && checkNull(passwordEntered)) {
                        final String email = emailEntered.getText().toString();
                        final String pw = passwordEntered.getText().toString();

                        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("User");
                        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        try {
                                            if (snapshot.child("email").getValue().toString().equalsIgnoreCase(email)) {
                                                if (pw.equals(registerActivity.decrypt(snapshot.child("password").getValue().toString()))) {
                                                    String role = snapshot.child("userType").getValue().toString();
                                                    String id = snapshot.child("userID").getValue().toString();
                                                    if (role.equals("customer")) {
                                                        //Directing user to their main screen
                                                        toast.cancel();
                                                        prefs.edit().putString("userID", id).apply();
                                                        startActivity(new Intent(loginActivity.this, MainActivity.class));
                                                    } else if (role.equals("seller")) {
                                                        //Directing user to their main screen
                                                        prefs.edit().putString("userID", id).apply();
                                                        startActivity(new Intent(loginActivity.this, SellerMainActivity.class));
                                                    } else if (role.equals("admin")) {
                                                        //Directing user to their main screen
                                                        prefs.edit().putString("userID", id).apply();
                                                        startActivity(new Intent(loginActivity.this, AdminMainActivity.class));
                                                    }
                                                } else if (!pw.equals(registerActivity.decrypt(snapshot.child("password").getValue().toString()))) {
                                                    if (counter == 0) {
                                                        String newPW = resetPWActivity.autoGeneratePassword(8);
                                                        resetPWActivity.resetPW(email, newPW);
                                                        resetPWActivity.sendMail(email, newPW);
                                                        startActivity(new Intent(loginActivity.this, loginActivity.class));
                                                    } else if (counter > 0) {
                                                        toast = Toast.makeText(loginActivity.this, "Login Failed. You have " + counter + " left.", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        counter --;
                                                    }
                                                }
                                            } else {
                                                toast = Toast.makeText(loginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }
                        });
                    }
                }
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, registerActivity.class));
            }
        });
    }

    private boolean checkNull(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Empty Field");
            return false;
        } else {
            return true;
        }
    }
}
