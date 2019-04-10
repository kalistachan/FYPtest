package com.example.fyptest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.fyptest.fragments.GroupFragment;
import com.example.fyptest.fragments.ProfileFragment;
import com.example.fyptest.fragments.PurchaseFragment;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    //private Toolbar maintoolbar;
    //will delete if not needed

    Fragment fragment;
    //List<Product> prodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*maintoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(maintoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        maintoolbar.setLogo(R.drawable.ic_logo_132dp_x_59dp);*/
        // will delete if not needed

        ImageButton homepagebutton = (ImageButton) findViewById(R.id.homepage);
        homepagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotohome = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(gotohome);
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);

        loadFragment(new PurchaseFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Catching Value thrown from login
        SharedPreferences prefs = getSharedPreferences("IDs", MODE_PRIVATE);
        String id = prefs.getString("userID", "UNKNOWN");
        Toast.makeText(this, id, Toast.LENGTH_LONG).show();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_purchase:
                    //  mTextMessage.setText(R.string.title_purchases);
                    fragment = new PurchaseFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_group:
                    // mTextMessage.setText(R.string.title_group);
                    fragment = new GroupFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_watchlist:
                    //mTextMessage.setText(R.string.title_watchlists);
                    return true;
                case R.id.navigation_profile:
                    fragment = new ProfileFragment();
                    //pushing userInfo out of current view
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
