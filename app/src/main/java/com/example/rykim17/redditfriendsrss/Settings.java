package com.example.rykim17.redditfriendsrss;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Button btnSave;
    Spinner fontSize;
    Spinner fontType;
    Spinner orderBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Toolbar stuff because it doesn't work by default WHYYYYY?! FUUUUUUU~
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setIcon(R.drawable.snoo);
        getSupportActionBar().setTitle("Predditor");
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initUIElements();
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        loadValues();
    }

    public void initUIElements() {
        btnSave = (Button)findViewById(R.id.btnSave);
        fontType = (Spinner)findViewById(R.id.spinnerFontType);
        fontSize = (Spinner)findViewById(R.id.spinnerFontSize);
        orderBy = (Spinner)findViewById(R.id.spinnerOrderBy);

        setSpinner(R.array.font_type, fontType);
        setSpinner(R.array.font_size, fontSize);
        setSpinner(R.array.order_by, orderBy);

        ClickHandler clickHandler = new ClickHandler();
        btnSave.setOnClickListener(clickHandler);
    }

    public void setSpinner(int resourceId, Spinner spinner){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, resourceId, R.layout.spinner_settings_layout);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public class ClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnSave:
                    savePreferences();
                    break;
            }
        }
    }

    public void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fontType", fontType.getSelectedItem().toString());
        editor.putInt("fontSize", Integer.parseInt(fontSize.getSelectedItem().toString()));
        editor.putBoolean("isDescending", getOrderByValue());
        editor.commit();
        setResult(42);
        finish();
    }

    public boolean getOrderByValue() {
        return orderBy.getSelectedItem().toString().equals("Posts ascending") ? false : true;
    }

    public void loadValues() {
        Resources res = getResources();

        String sFontType = sharedPreferences.getString("fontType", "Arial");
        String sFontSize = Integer.toString(sharedPreferences.getInt("fontSize", 12));
        String sIsDescending = sharedPreferences.getBoolean("isDescending", true) == true ? "Posts descending" : "Posts ascending";

        fontType.setSelection(((ArrayAdapter)fontType.getAdapter()).getPosition(sFontType));
        fontSize.setSelection(((ArrayAdapter)fontSize.getAdapter()).getPosition(sFontSize));
        orderBy.setSelection(((ArrayAdapter)orderBy.getAdapter()).getPosition(sIsDescending));

    }
}
