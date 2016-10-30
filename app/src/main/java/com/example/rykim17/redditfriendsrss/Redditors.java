package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Redditors extends AppCompatActivity {
    ArrayList<String> redditors;
    AlertDialog addDialog;

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
            // Show sad face
        } else {
            // Show list view
        }

        initDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addUser:
                addDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.redditors_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Redditor");
        EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Redditors.this, "Added!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        addDialog = builder.create();
    }
}
