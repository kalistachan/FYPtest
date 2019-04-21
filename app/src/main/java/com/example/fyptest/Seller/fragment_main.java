package com.example.fyptest.Seller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fyptest.R;

public class fragment_main extends Fragment {

    SharedPreferences preferences;
    String userIdentity;

    TextView textView;

    public fragment_main() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getContext().getSharedPreferences("IDs", Context.MODE_PRIVATE);
        userIdentity = preferences.getString("userID", null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragment_main, container, false);

        textView = (TextView) view.findViewById(R.id.textView);

        textView.setText(userIdentity);

        return view;
    }


}
