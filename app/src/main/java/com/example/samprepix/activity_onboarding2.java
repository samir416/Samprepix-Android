package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_onboarding2 extends AppCompatActivity {

    LinearLayout levelBeginner;
    LinearLayout levelIntermediate;
    LinearLayout levelAdvanced;

    Button btnBack;
    Button btnNext;

    SharedPreferences preferences;

    String selectedLevel = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding2);

        levelBeginner = findViewById(R.id.level_beginner);
        levelIntermediate = findViewById(R.id.level_intermediate);
        levelAdvanced = findViewById(R.id.level_advanced);

        btnBack = findViewById(R.id.btn_back);
        btnNext = findViewById(R.id.btn_next);

        preferences = getSharedPreferences(
                "onboarding_data",
                MODE_PRIVATE
        );

        levelBeginner.setOnClickListener(v -> {
            selectedLevel = "Beginner";
            selectLevel(levelBeginner);
        });

        levelIntermediate.setOnClickListener(v -> {
            selectedLevel = "Intermediate";
            selectLevel(levelIntermediate);
        });

        levelAdvanced.setOnClickListener(v -> {
            selectedLevel = "Advanced";
            selectLevel(levelAdvanced);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnNext.setOnClickListener(v -> {

            if (selectedLevel.isEmpty()) {

                Toast.makeText(
                        activity_onboarding2.this,
                        "Select your experience level",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                SharedPreferences.Editor editor =
                        preferences.edit();

                editor.putString(
                        "experience_level",
                        selectedLevel
                );

                editor.apply();

                Intent intent = new Intent(
                        activity_onboarding2.this,
                        activity_onboarding3.class
                );

                startActivity(intent);
                finish();
            }
        });
    }

    private void selectLevel(LinearLayout selectedLayout) {

        levelBeginner.setBackgroundResource(
                R.drawable.card_bg
        );

        levelIntermediate.setBackgroundResource(
                R.drawable.card_bg
        );

        levelAdvanced.setBackgroundResource(
                R.drawable.card_bg
        );

        selectedLayout.setBackgroundResource(
                R.drawable.btn_outline
        );
    }
}