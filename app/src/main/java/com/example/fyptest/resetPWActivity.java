package com.example.fyptest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.fyptest.database.userClass;
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
                db = FirebaseDatabase.getInstance().getReference("User");
                final String emailString = enteredEmail.getText().toString().trim();
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (emailString.equals(snapshot.child("email").getValue().toString())) {
                                String userID = snapshot.child("userID").getValue().toString();
                                int pwLength = 8;
                                DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("User").child(userID);
//
                                String email = snapshot.child("email").getValue().toString();
                                String newPW = autoGeneratePassword(pwLength);
                                String contactNum = snapshot.child("contactNum").getValue().toString();
                                String userType = snapshot.child("userType").getValue().toString();

                                userClass userClass = new userClass(userID,email, newPW, contactNum, userType);
                                newDB.setValue(userClass);
                                startActivity(new Intent(resetPWActivity.this, loginActivity.class));
                                break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            return;
                        }
                    });
                }
        });
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
