package com.example.juego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private Button btnRaceMode;
    private Button btnLegendaryMode;
    private Button btnPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnRaceMode = findViewById(R.id.btn_race_mode);
        btnLegendaryMode = findViewById(R.id.btn_legendary_mode);
        btnPreferences = findViewById(R.id.btn_preferences);

        btnRaceMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRaceMode();
            }
        });

        btnLegendaryMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLegendaryMode();
            }
        });

        btnPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPreferences();
            }
        });
    }

    private void startRaceMode() {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void startLegendaryMode() {
        Intent intent = new Intent(MenuActivity.this, LegendaryModeActivity.class);
        startActivity(intent);
    }

    private void openPreferences() {
        Intent intent = new Intent(MenuActivity.this, PreferencesActivity.class);
        startActivity(intent);
    }
}