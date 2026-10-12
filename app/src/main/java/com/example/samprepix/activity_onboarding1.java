package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_onboarding1 extends AppCompatActivity {

    private LinearLayout roleFrontend;
    private LinearLayout roleFullstack;
    private LinearLayout roleBackend;
    private LinearLayout roleMobile;
    private LinearLayout roleData;
    private LinearLayout roleOther;

    private EditText etOtherRole;

    private Button btnNext;

    private SharedPreferences preferences;

    private String selectedRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_onboarding1
        );

        roleFrontend =
                findViewById(R.id.role_frontend);

        roleFullstack =
                findViewById(R.id.role_fullstack);

        roleBackend =
                findViewById(R.id.role_backend);

        roleMobile =
                findViewById(R.id.role_mobile);

        roleData =
                findViewById(R.id.role_data);

        roleOther =
                findViewById(R.id.role_other);

        etOtherRole =
                findViewById(R.id.et_other_role);

        btnNext =
                findViewById(R.id.btn_next);

        preferences =
                getSharedPreferences(
                        "onboarding_data",
                        MODE_PRIVATE
                );

        loadSavedRole();

        roleFrontend.setOnClickListener(
                v -> selectRole(
                        roleFrontend,
                        "Frontend Developer"
                )
        );

        roleFullstack.setOnClickListener(
                v -> selectRole(
                        roleFullstack,
                        "Full Stack Developer"
                )
        );

        roleBackend.setOnClickListener(
                v -> selectRole(
                        roleBackend,
                        "Backend Developer"
                )
        );

        roleMobile.setOnClickListener(
                v -> selectRole(
                        roleMobile,
                        "Mobile App Developer"
                )
        );

        roleData.setOnClickListener(
                v -> selectRole(
                        roleData,
                        "Data Analyst"
                )
        );

        roleOther.setOnClickListener(
                v -> {

                    selectedRole = "Other";

                    clearSelection();

                    roleOther.setBackgroundResource(
                            R.drawable.btn_outline
                    );

                    etOtherRole.setVisibility(
                            EditText.VISIBLE
                    );

                    etOtherRole.requestFocus();
                }
        );

        btnNext.setOnClickListener(
                v -> saveAndContinue()
        );

        findViewById(
                R.id.tv_skip
        ).setOnClickListener(
                v -> skipOnboardingStep()
        );

        findViewById(
                R.id.tv_back
        ).setOnClickListener(
                v -> goBackToLogin()
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goBackToLogin();
                    }
                }
        );
    }

    private void selectRole(
            LinearLayout selectedLayout,
            String role
    ) {

        selectedRole = role;

        clearSelection();

        selectedLayout.setBackgroundResource(
                R.drawable.btn_outline
        );

        etOtherRole.setVisibility(
                EditText.GONE
        );
    }

    private void clearSelection() {

        roleFrontend.setBackgroundResource(
                R.drawable.card_bg
        );

        roleFullstack.setBackgroundResource(
                R.drawable.card_bg
        );

        roleBackend.setBackgroundResource(
                R.drawable.card_bg
        );

        roleMobile.setBackgroundResource(
                R.drawable.card_bg
        );

        roleData.setBackgroundResource(
                R.drawable.card_bg
        );

        roleOther.setBackgroundResource(
                R.drawable.card_bg
        );
    }

    private void loadSavedRole() {

        String savedRole =
                preferences.getString(
                        "target_role",
                        ""
                );

        if (savedRole.isEmpty()) {
            return;
        }

        if (savedRole.equals(
                "Frontend Developer"
        )) {

            selectRole(
                    roleFrontend,
                    savedRole
            );

        } else if (
                savedRole.equals(
                        "Full Stack Developer"
                )
        ) {

            selectRole(
                    roleFullstack,
                    savedRole
            );

        } else if (
                savedRole.equals(
                        "Backend Developer"
                )
        ) {

            selectRole(
                    roleBackend,
                    savedRole
            );

        } else if (
                savedRole.equals(
                        "Mobile App Developer"
                )
        ) {

            selectRole(
                    roleMobile,
                    savedRole
            );

        } else if (
                savedRole.equals(
                        "Data Analyst"
                )
        ) {

            selectRole(
                    roleData,
                    savedRole
            );

        } else {

            selectedRole = "Other";

            clearSelection();

            roleOther.setBackgroundResource(
                    R.drawable.btn_outline
            );

            etOtherRole.setVisibility(
                    EditText.VISIBLE
            );

            etOtherRole.setText(
                    savedRole
            );
        }
    }

    private void saveAndContinue() {

        String role =
                selectedRole;

        if (role.equals("Other")) {

            String customRole =
                    etOtherRole.getText()
                            .toString()
                            .trim();

            if (customRole.isEmpty()) {

                Toast.makeText(
                        this,
                        "Please enter your target role",
                        Toast.LENGTH_SHORT
                ).show();

                etOtherRole.requestFocus();
                return;
            }

            role = customRole;
        }

        if (role.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please select your target role",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String email =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                ).getString(
                        "email",
                        ""
                );

        preferences.edit()
                .putString(
                        "target_role",
                        role
                )
                .putBoolean(
                        "step1_completed",
                        true
                )
                .apply();

        if (!email.isEmpty()) {

            activity_login.DatabaseHelper database =
                    new activity_login.DatabaseHelper(
                            this
                    );

            database.updateProfile(
                    email,
                    preferences.getString(
                            "journey_type",
                            ""
                    ),
                    role,
                    preferences.getString(
                            "experience_level",
                            ""
                    ),
                    preferences.getString(
                            "current_role",
                            ""
                    ),
                    preferences.getString(
                            "designation",
                            ""
                    ),
                    preferences.getInt(
                            "years_of_experience",
                            0
                    ),
                    preferences.getString(
                            "career_goal",
                            ""
                    )
            );
        }

        startActivity(
                new Intent(
                        activity_onboarding1.this,
                        activity_onboarding2.class
                )
        );

        finish();
    }

    private void skipOnboardingStep() {

        preferences.edit()
                .putBoolean(
                        "onboarding1_skipped",
                        true
                )
                .apply();

        startActivity(
                new Intent(
                        activity_onboarding1.this,
                        activity_onboarding2.class
                )
        );

        finish();
    }

    private void goBackToLogin() {

        Intent intent =
                new Intent(
                        activity_onboarding1.this,
                        activity_login.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }
}