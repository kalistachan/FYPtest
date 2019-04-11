package com.example.fyptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginActivity extends AppCompatActivity {

    EditText emailEntered, passwordEntered;
    Button buttonLogin, buttonRegister;
    TextView forgotPassword;

    SharedPreferences prefs;

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

        counter = 5;

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
                        userDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    if (email.equals(snapshot.child("email").getValue().toString())) {
                                        if (pw.equals(snapshot.child("password").getValue().toString())) {
                                            String role = snapshot.child("userType").getValue().toString();
                                            String id = snapshot.child("userID").getValue().toString();
                                            if (role.equals("customer")) {
                                                //Directing user to their main screen
                                                prefs.edit().putString("userID", id).apply();
                                                startActivity(new Intent(loginActivity.this, MainActivity.class));
                                                return;
                                            } if (role.equals("seller")) {
                                                return;
                                            } if (role.equals("admin")) {
                                                return;
                                            }
                                        } if (!pw.equals(snapshot.child("password").getValue().toString())) {
                                            if (counter == 0) {
                                                blockAcc(emailEntered);
                                                return;
                                            } if (counter > 0) {
                                                toast(2, counter);
                                                counter --;
                                                return;
                                            }
                                        }
                                    }
                                } toast(1, counter);
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

    private void toast(int a, int counter) {
        if (a == 1) {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_LONG).show();
        } else if (a == 2) {
            Toast.makeText(this, "Login Fails. You have " + counter + " left.", Toast.LENGTH_LONG).show();
        }
    }

    private void blockAcc(EditText editText) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Block Account");
    }

    private void removeBlock(EditText editText) {}
}
