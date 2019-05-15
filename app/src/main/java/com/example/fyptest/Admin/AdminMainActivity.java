package com.example.fyptest.Admin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fyptest.R;
import com.example.fyptest.Seller.fragment_main;
import com.example.fyptest.fragments.ProductListingFragment;
import com.example.fyptest.fragments.SearchFragment;
import com.example.fyptest.loginActivity;

public class AdminMainActivity extends AppCompatActivity {
    SharedPreferences preferences;
    String userIdentity;

    Toolbar toolbar;
    Fragment fragment;

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setLogo(R.drawable.logosmall);

        View homepage = toolbar.getChildAt(0);
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new AdminMainFragment();
                loadFragment(fragment);
            }
        });

        //Set to view main screen on application start-up
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, new AdminMainFragment());
        ft.commit();

        //Identifying User
        try {
            this.preferences = getSharedPreferences("IDs", MODE_PRIVATE);
            this.userIdentity = preferences.getString("userID", null);
        } catch (Exception e) {
            Log.d("Error in PurchaseFragment : ", e.toString());
            startActivity(new Intent(AdminMainActivity.this, loginActivity.class));
        }

        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.clear();
                edit.apply();
                startActivity(new Intent(AdminMainActivity.this, loginActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_toolbar, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Enter Product Information");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                TextView textView=(TextView)findViewById(R.id.action_search);
                textView.setText(newText);
                swapToSearchFragment(newText);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                TextView textView=(TextView)findViewById(R.id.action_search);
                textView.setText(query);

                swapToSearchFragment(query);
                return true;
            }
        };

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getSupportFragmentManager().popBackStack();
                return false;
            }
        });

        menuItemSearch.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                getSupportFragmentManager().popBackStack();
                return true;
            }
        });

        searchView.setOnQueryTextListener(queryTextListener);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSeller:
                fragment = new AddSellerFragment();
                loadFragment(fragment);
                return true;

            /*case R.id.toolbar_logo:
                fragment = new AdminMainFragment();
                loadFragment(fragment);
                return true;*/
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void swapToSearchFragment(String queryText) {
        SearchFragment newSearchFragment = new SearchFragment();
        Bundle arguments = new Bundle();
        arguments.putString("query" , queryText);
        newSearchFragment.setArguments(arguments);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, newSearchFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
