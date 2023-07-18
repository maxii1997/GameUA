package com.example.juego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity {

    private EditText instructionCountEditText;
    private EditText initialSpeedEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        instructionCountEditText = findViewById(R.id.instructionCountEditText);
        initialSpeedEditText = findViewById(R.id.initialSpeedEditText);

        sharedPreferences = getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);

        loadPreferences();
    }

    private void loadPreferences() {
        int instructionCount = sharedPreferences.getInt("instructionCount", 3);
        int initialSpeed = sharedPreferences.getInt("initialSpeed", 3);

        instructionCountEditText.setText(String.valueOf(instructionCount));
        initialSpeedEditText.setText(String.valueOf(initialSpeed));
    }

    private void savePreferences() {
        int instructionCount = Integer.parseInt(instructionCountEditText.getText().toString());
        int initialSpeed = Integer.parseInt(initialSpeedEditText.getText().toString());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("instructionCount", instructionCount);
        editor.putInt("initialSpeed", initialSpeed);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }
}