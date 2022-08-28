package com.example.stjerneord;

import static android.graphics.Color.rgb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
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
                System.out.println("---- STATUS ----");
                System.out.println(LeftHandedMode);
            }
        });

        SharedPreferences prefs = this.getSharedPreferences("com.example.app", Context.MODE_PRIVATE);
        LeftHandedMode = prefs.getBoolean("LeftHandedMode", false);
        language = prefs.getInt("language", 0);
        LeftHandedModeToggle.setChecked(LeftHandedMode);
    }

    @Override
    public void onPause() {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("LeftHandedMode", LeftHandedMode).apply();
        prefs.edit().putInt("language", language).apply();
        System.out.println("--- SAVED INSTACE!!! ---");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("LeftHandedMode", LeftHandedMode);
        savedInstanceState.putInt("language", language);
        System.out.println("--- SAVED INSTACE!!! ---");
        super.onSaveInstanceState(savedInstanceState);
    }





}
