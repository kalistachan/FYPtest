package com.example.fyptest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;
import com.example.fyptest.database.customerClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class resetPWActivity extends AppCompatActivity {

    Button submit, back2login;
    EditText enteredEmail;

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pw);

        enteredEmail = (EditText) findViewById(R.id.enteredEmail);
        submit = (Button) findViewById(R.id.submit);
        back2login = (Button) findViewById(R.id.back2login);

        back2login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(resetPWActivity.this, loginActivity.class));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkNull(enteredEmail)) {
                    db = FirebaseDatabase.getInstance().getReference("User").child("customer");
                    final String emailString = enteredEmail.getText().toString().trim();
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.child("cus_email").getValue().toString().equals(emailString)) {
                                DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("User").child("Customer").child(snapshot.child("cus_ID").getValue().toString());
                                String newPW = autoGeneratePassword(8);

                                String a = snapshot.child("cus_ID").getValue().toString();
                                String b = snapshot.child("cus_email").getValue().toString();
                                String d = snapshot.child("cus_contactNum").getValue().toString();
                                String e = snapshot.child("cus_firstName").getValue().toString();
                                String f = snapshot.child("cus_LastName").getValue().toString();
                                String g = snapshot.child("cus_address").getValue().toString();
                                String h = snapshot.child("cus_postalCode").getValue().toString();
                                int i = snapshot.child("cus_loyaltyPoint").getValue(Integer.class);
                                String j = snapshot.child("cus_ut_ID").getValue().toString();

                                customerClass customerClass = new customerClass(a, b, d, e,
                                        f, newPW, g, h, i, j);

                                newDB.setValue(customerClass);
                                //TextView resetPW = (TextView) findViewById(R.id.resetPW);
                                //resetPW.setText(snapshot.child("cus_email").getValue().toString());
                            } else {
                                enteredEmail.setError("Email not registered");
                                //TextView resetPW = (TextView) findViewById(R.id.resetPW);
                                //resetPW.setText(snapshot.child("cus_email").getValue().toString());
                            }
                        }}
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    } //Not Complete

    public static String autoGeneratePassword(int passwordLength) {
        String PASSWORD_CHARACTER ="0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV@!&";
        Random random = new Random();
        StringBuilder password = new StringBuilder(passwordLength);

        for (int i = 0; i < passwordLength; i++) {
            password.append(PASSWORD_CHARACTER.charAt(random.nextInt(PASSWORD_CHARACTER.length())));
        }
        return password.toString();
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


    //Example of updating for firebase
    /*private boolean updateArtist(String id, String name, String genre) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artist").child(id);

        Artist artist = new Artist(id, name, genre);

        databaseReference.setValue(artist);

        Toast.makeText(this, "Artist Updated Successful", Toast.LENGTH_LONG).show();

        return true;
    }*/
}
