package com.example.rykim17.redditfriendsrss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {
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
    }

    public void initUIElements() {
        btnSave = (Button)findViewById(R.id.btnSave);
        fontType = (Spinner)findViewById(R.id.spinnerFontType);
        fontSize = (Spinner)findViewById(R.id.spinnerFontSize);
        orderBy = (Spinner)findViewById(R.id.spinnerOrderBy);

        setSpinner(R.array.font_type, fontType);
        setSpinner(R.array.font_size, fontSize);
        setSpinner(R.array.order_by, orderBy);
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

    }
}
