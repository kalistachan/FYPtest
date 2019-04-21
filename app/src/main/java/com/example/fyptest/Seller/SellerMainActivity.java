package com.example.fyptest.Seller;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyptest.R;

public class SellerMainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    String userIdentity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_main);

        preferences = getSharedPreferences("IDs", MODE_PRIVATE);
        userIdentity = preferences.getString("userID", "UNKNOWN");

        Toast.makeText(this, userIdentity, Toast.LENGTH_LONG).show();
    }
}
