package com.example.rykim17.redditfriendsrss;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class Redditors extends AppCompatActivity implements DialogAddRedditor.NoticeDialogListener {
    ArrayList<String> redditors;
    DialogFragment addRedditor;
    SharedPreferences sharedPreferences;
    ArrayAdapter adapter;
    ListView listView;
    RedditorAdapter redditorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redditors);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initRedditorData();

        addRedditor = new DialogAddRedditor();
        listView = (ListView)findViewById(R.id.listViewRedditors);
        redditorAdapter = new RedditorAdapter(this, R.layout.redditor_template, redditors);
        listView.setAdapter(redditorAdapter);
        setResult(1);
    }

    @Override
    public void onDialogPositiveClick(View template) {
        EditText redditorInput = (EditText)template.findViewById(R.id.redditorName);
        String redditor = redditorInput.getText().toString().trim();

        // Check to see if the value has any commas.
        if(redditor.contains(",")) {
            Toast.makeText(this, "Invalid Redditor username. Cannot contain commas.", Toast.LENGTH_SHORT).show();
        } else if(redditors.contains(redditor)) {
            Toast.makeText(this, "Invalid Redditor username. Cannot contain duplicates.", Toast.LENGTH_SHORT).show();
        } else {
            redditors.add(redditor);
            SharedPreferences.Editor editor =  sharedPreferences.edit();
            String putString = "";
            String comma = "";

            for(int i = 0; i < redditors.size(); i++) {
                putString += comma + redditors.get(i);
                comma = ",";
            }

            editor.putString("redditors", putString);
            editor.commit();
            redditorAdapter.notifyDataSetChanged();
            setResult(42);
        }
    }

    public void initRedditorData() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sharedPrefRedditors = sharedPreferences.getString("redditors", "");

        if(sharedPrefRedditors.equals("")) {
            redditors = new ArrayList<String>();
        } else {
            redditors = new ArrayList<String>(Arrays.asList(sharedPrefRedditors.split(",")));

            for(int i = 0; i < redditors.size(); i++) {
                Log.d("test", redditors.get(i));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addUser:
                addRedditor.show(getFragmentManager(), "Add Redditor");
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

    public class RedditorAdapter extends ArrayAdapter<String> {
        ArrayList<String> redditors;
        SharedPreferences sharedPreferences;

        public RedditorAdapter(Context context, int resource, ArrayList<String> redditors) {
            super(context, resource, redditors);
            this.redditors = redditors;
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.redditor_template, null);
            }

            TextView name = (TextView)v.findViewById(R.id.templateRedditorName);
            ImageView btnRemove = (ImageView)v.findViewById(R.id.btnTemplateRedditorRemove);
            name.setText(this.redditors.get(position));
            btnRemove.setTag(position);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.btnTemplateRedditorRemove:
                            removeItem((int)v.getTag());
                            break;
                    }
                }
            });

            return v;
        }

        public void removeItem(int position) {
            String sRedditors = "";
            String comma = "";

            this.redditors.remove(position);

            for(int i = 0; i < redditors.size(); i++) {
                sRedditors += comma + redditors.get(i);
                comma = ",";
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("redditors", sRedditors);
            editor.commit();
            notifyDataSetChanged();
            setResult(42);
        }
    }
}
