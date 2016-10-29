package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class Redditors extends AppCompatActivity {
    ArrayList<String> redditors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redditors);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String redditors = sharedPreferences.getString("redditors", "");

        if(redditors.equals("")) {

        } else {

        }
    }
}
