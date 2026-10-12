package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_onboarding2 extends AppCompatActivity {

    private LinearLayout levelBeginner;
    private LinearLayout levelIntermediate;
    private LinearLayout levelAdvanced;

    private Button btnBack;
    private Button btnNext;

    private SharedPreferences preferences;

    private String selectedLevel = "";

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

        loadSavedLevel();

        levelBeginner.setOnClickListener(
                v -> selectLevel(
                        levelBeginner,
                        "Beginner"
                )
        );

        levelIntermediate.setOnClickListener(
                v -> selectLevel(
                        levelIntermediate,
                        "Intermediate"
                )
        );

        levelAdvanced.setOnClickListener(
                v -> selectLevel(
                        levelAdvanced,
                        "Advanced"
                )
        );

        findViewById(R.id.tv_back)
                .setOnClickListener(
                        v -> goBack()
                );

        btnBack.setOnClickListener(
                v -> goBack()
        );

        btnNext.setOnClickListener(
                v -> goNext()
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goBack();
                    }
                }
        );
    }

    private void selectLevel(
            LinearLayout selectedLayout,
            String level
    ) {

        selectedLevel = level;

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

    private void loadSavedLevel() {

        String savedLevel =
                preferences.getString(
                        "experience_level",
                        ""
                );

        if (savedLevel.isEmpty()) {
            return;
        }

        if (savedLevel.equalsIgnoreCase("Beginner")) {

            selectLevel(
                    levelBeginner,
                    "Beginner"
            );

        } else if (savedLevel.equalsIgnoreCase("Intermediate")) {

            selectLevel(
                    levelIntermediate,
                    "Intermediate"
            );

        } else if (savedLevel.equalsIgnoreCase("Advanced")) {

            selectLevel(
                    levelAdvanced,
                    "Advanced"
            );
        }
    }

    private void goNext() {

        if (selectedLevel.isEmpty()) {

            Toast.makeText(
                    this,
                    "Select your experience level",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        preferences.edit()
                .putString(
                        "experience_level",
                        selectedLevel
                )
                .apply();

        Intent intent =
                new Intent(
                        activity_onboarding2.this,
                        activity_onboarding3.class
                );

        startActivity(intent);
        finish();
    }

    private void goBack() {

        Intent intent =
                new Intent(
                        activity_onboarding2.this,
                        activity_onboarding1.class
                );

        startActivity(intent);
        finish();
    }
}