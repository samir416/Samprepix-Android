package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_onboarding1 extends AppCompatActivity {

    LinearLayout roleFrontend;
    LinearLayout roleFullstack;
    LinearLayout roleBackend;
    LinearLayout roleMobile;
    LinearLayout roleData;
    LinearLayout roleOther;

    Button btnnext;

    SharedPreferences preferences;

    String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding1);

        roleFrontend = findViewById(R.id.role_frontend);
        roleFullstack = findViewById(R.id.role_fullstack);
        roleBackend = findViewById(R.id.role_backend);
        roleMobile = findViewById(R.id.role_mobile);
        roleData = findViewById(R.id.role_data);
        roleOther = findViewById(R.id.role_other);

        btnnext = findViewById(R.id.btn_next);

        preferences = getSharedPreferences(
                "onboarding_data",
                MODE_PRIVATE
        );

        roleFrontend.setOnClickListener(v -> {
            selectedRole = "Frontend Developer";
            selectRole(roleFrontend);
        });

        roleFullstack.setOnClickListener(v -> {
            selectedRole = "Full Stack Developer";
            selectRole(roleFullstack);
        });

        roleBackend.setOnClickListener(v -> {
            selectedRole = "Backend Developer";
            selectRole(roleBackend);
        });

        roleMobile.setOnClickListener(v -> {
            selectedRole = "Mobile App Developer";
            selectRole(roleMobile);
        });

        roleData.setOnClickListener(v -> {
            selectedRole = "Data Analyst";
            selectRole(roleData);
        });

        roleOther.setOnClickListener(v -> {
            selectedRole = "Other";
            selectRole(roleOther);
        });

        btnnext.setOnClickListener(v -> {

            if (selectedRole.isEmpty()) {

                Toast.makeText(
                        activity_onboarding1.this,
                        "Select your target role",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                SharedPreferences.Editor editor =
                        preferences.edit();

                editor.putString(
                        "target_role",
                        selectedRole
                );

                editor.apply();

                Intent intent = new Intent(
                        activity_onboarding1.this,
                        activity_onboarding2.class
                );

                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.tv_skip).setOnClickListener(v -> {

            SharedPreferences.Editor editor =
                    preferences.edit();

            editor.putBoolean(
                    "onboarding1_skipped",
                    true
            );

            editor.apply();

            Intent intent = new Intent(
                    activity_onboarding1.this,
                    activity_onboarding2.class
            );

            startActivity(intent);
            finish();
        });

        findViewById(R.id.tv_back).setOnClickListener(v -> {
            finish();
        });
    }

    private void selectRole(LinearLayout selectedLayout) {

        roleFrontend.setBackgroundResource(R.drawable.card_bg);
        roleFullstack.setBackgroundResource(R.drawable.card_bg);
        roleBackend.setBackgroundResource(R.drawable.card_bg);
        roleMobile.setBackgroundResource(R.drawable.card_bg);
        roleData.setBackgroundResource(R.drawable.card_bg);
        roleOther.setBackgroundResource(R.drawable.card_bg);

        selectedLayout.setBackgroundResource(
                R.drawable.btn_outline
        );
    }
}