package com.example.fyptest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fyptest.Email.GMailSender;
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
                    String emailString = enteredEmail.getText().toString().trim();
                    String newPW = autoGeneratePassword(8);
                    String emailSubject = "A new password for your 4GB account had been generated";
                    String emailBody = "Your new password is";
                    resetPW(emailString, newPW);
                    sendMail(emailString, newPW, emailSubject, emailBody);
                    startActivity(new Intent(resetPWActivity.this, loginActivity.class).putExtra("IntentSource", "pwReset"));
                }
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

    public static void resetPW(final String emailString, final String resetPW) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("User");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (emailString.equals(snapshot.child("email").getValue().toString())) {
                        String userID = snapshot.child("userID").getValue().toString();
                        try {
                            String encryptPW = registerActivity.encrypt(resetPW);
                            DatabaseReference newDB = FirebaseDatabase.getInstance().getReference("User").child(userID).child("password");
                            newDB.setValue(encryptPW);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                return;
            }
        });
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

    public static void sendMail(final String email,final String resetPW, final String Subject, final String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("fourgroupbuying@gmail.com", "password4gb");
                    sender.sendMail(Subject,
                            body + " : " + resetPW,
                            "fourgroupbuying@gmail.com", email);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }

    public static void sendMailRelatedToProduct(final String Recipient, final String Subject, final String Body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender("fourgroupbuying@gmail.com", "password4gb");
                    sender.sendMail(Subject,
                            Body,
                            "fourgroupbuying@gmail.com", Recipient);
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();
    }
}
