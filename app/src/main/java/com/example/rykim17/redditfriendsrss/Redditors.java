package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.ArrayList;

public class Redditors extends AppCompatActivity {
    ArrayList<String> redditors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redditors);

        // Toolbar stuff because it doesn't work by default WHYYYYY?! FUUUUUUU~
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String redditors = sharedPreferences.getString("redditors", "");

        if(redditors.equals("")) {

        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.redditors_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
