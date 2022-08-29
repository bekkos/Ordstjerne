package com.example.stjerneord;

import static android.graphics.Color.rgb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.sql.SQLOutput;

public class SettingsActivity extends AppCompatActivity {

    private boolean LeftHandedMode;
    private int language;
    Switch LeftHandedModeToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();
        getWindow().setStatusBarColor(rgb(44, 62, 80));

        LeftHandedModeToggle = (Switch) findViewById(R.id.LeftHandedModeToggle);
        LeftHandedModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    LeftHandedMode = true;
                } else {
                    LeftHandedMode = false;
                }
            }
        });

        SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        LeftHandedMode = prefs.getBoolean("LeftHandedMode", false);
        language = prefs.getInt("language", 0);
        RadioGroup rg = (RadioGroup) findViewById(R.id.languageSetting);
        if(language == 0) {
            rg.check(R.id.rbNorwegian);
        } else {
            rg.check(R.id.rbEnglish);
        }
        LeftHandedModeToggle.setChecked(LeftHandedMode);
    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbNorwegian:
                if (checked)
                    language = 0;
                    break;
            case R.id.rbEnglish:
                if (checked)
                    language = 1;
                    break;
        }
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = this.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LeftHandedMode", LeftHandedMode).apply();
        prefs.edit().putInt("language", language).apply();
        System.out.println("--- SAVED INSTACE!!! ---");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {

        System.out.println("--- SAVED INSTACE!!! ---");
        super.onSaveInstanceState(savedInstanceState);
    }





}
