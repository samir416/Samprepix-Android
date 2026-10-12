package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_onboarding3 extends AppCompatActivity {

    private LinearLayout techJava;
    private LinearLayout techSpring;
    private LinearLayout techJavaScript;
    private LinearLayout techReact;
    private LinearLayout techNode;
    private LinearLayout techMongoDb;
    private LinearLayout techPython;
    private LinearLayout techCpp;
    private LinearLayout techOther;

    private EditText etOtherTech;

    private Button btnBack;
    private Button btnFinish;

    private SharedPreferences preferences;

    private String selectedTech = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding3);

        techJava = findViewById(R.id.tech_java);
        techSpring = findViewById(R.id.tech_spring);
        techJavaScript = findViewById(R.id.tech_javascript);
        techReact = findViewById(R.id.tech_react);
        techNode = findViewById(R.id.tech_node);
        techMongoDb = findViewById(R.id.tech_mongodb);
        techPython = findViewById(R.id.tech_python);
        techCpp = findViewById(R.id.tech_cpp);
        techOther = findViewById(R.id.tech_other);

        etOtherTech = findViewById(R.id.et_other_tech);

        btnBack = findViewById(R.id.btn_back);
        btnFinish = findViewById(R.id.btn_finish);

        preferences = getSharedPreferences(
                "onboarding_data",
                MODE_PRIVATE
        );

        loadSavedTechnology();

        techJava.setOnClickListener(
                v -> selectTechnology(
                        techJava,
                        "Java"
                )
        );

        techSpring.setOnClickListener(
                v -> selectTechnology(
                        techSpring,
                        "Spring Boot"
                )
        );

        techJavaScript.setOnClickListener(
                v -> selectTechnology(
                        techJavaScript,
                        "JavaScript"
                )
        );

        techReact.setOnClickListener(
                v -> selectTechnology(
                        techReact,
                        "React"
                )
        );

        techNode.setOnClickListener(
                v -> selectTechnology(
                        techNode,
                        "Node.js"
                )
        );

        techMongoDb.setOnClickListener(
                v -> selectTechnology(
                        techMongoDb,
                        "MongoDB"
                )
        );

        techPython.setOnClickListener(
                v -> selectTechnology(
                        techPython,
                        "Python"
                )
        );

        techCpp.setOnClickListener(
                v -> selectTechnology(
                        techCpp,
                        "C++"
                )
        );

        techOther.setOnClickListener(v -> {

            selectedTech = "";

            clearSelection();

            techOther.setBackgroundResource(
                    R.drawable.btn_outline
            );

            etOtherTech.setVisibility(
                    View.VISIBLE
            );

            etOtherTech.requestFocus();
        });

        findViewById(R.id.tv_back)
                .setOnClickListener(
                        v -> goBack()
                );

        findViewById(R.id.tv_skip)
                .setOnClickListener(
                        v -> finishOnboarding("")
                );

        btnBack.setOnClickListener(
                v -> goBack()
        );

        btnFinish.setOnClickListener(
                v -> finishOnboarding(
                        selectedTech
                )
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

    private void selectTechnology(
            LinearLayout layout,
            String technology
    ) {

        selectedTech = technology;

        clearSelection();

        layout.setBackgroundResource(
                R.drawable.btn_outline
        );

        etOtherTech.setVisibility(
                View.GONE
        );

        etOtherTech.setText("");
    }

    private void clearSelection() {

        techJava.setBackgroundResource(
                R.drawable.card_bg
        );

        techSpring.setBackgroundResource(
                R.drawable.card_bg
        );

        techJavaScript.setBackgroundResource(
                R.drawable.card_bg
        );

        techReact.setBackgroundResource(
                R.drawable.card_bg
        );

        techNode.setBackgroundResource(
                R.drawable.card_bg
        );

        techMongoDb.setBackgroundResource(
                R.drawable.card_bg
        );

        techPython.setBackgroundResource(
                R.drawable.card_bg
        );

        techCpp.setBackgroundResource(
                R.drawable.card_bg
        );

        techOther.setBackgroundResource(
                R.drawable.card_bg
        );
    }

    private void loadSavedTechnology() {

        String savedTech =
                preferences.getString(
                        "tech_stack",
                        ""
                );

        if (savedTech.isEmpty()) {
            return;
        }

        if (savedTech.equals("Java")) {

            selectTechnology(
                    techJava,
                    savedTech
            );

        } else if (savedTech.equals("Spring Boot")) {

            selectTechnology(
                    techSpring,
                    savedTech
            );

        } else if (savedTech.equals("JavaScript")) {

            selectTechnology(
                    techJavaScript,
                    savedTech
            );

        } else if (savedTech.equals("React")) {

            selectTechnology(
                    techReact,
                    savedTech
            );

        } else if (savedTech.equals("Node.js")) {

            selectTechnology(
                    techNode,
                    savedTech
            );

        } else if (savedTech.equals("MongoDB")) {

            selectTechnology(
                    techMongoDb,
                    savedTech
            );

        } else if (savedTech.equals("Python")) {

            selectTechnology(
                    techPython,
                    savedTech
            );

        } else if (savedTech.equals("C++")) {

            selectTechnology(
                    techCpp,
                    savedTech
            );

        } else {

            selectedTech = savedTech;

            clearSelection();

            techOther.setBackgroundResource(
                    R.drawable.btn_outline
            );

            etOtherTech.setVisibility(
                    View.VISIBLE
            );

            etOtherTech.setText(
                    savedTech
            );
        }
    }

    private void finishOnboarding(
            String technology
    ) {

        if (etOtherTech.getVisibility() ==
                View.VISIBLE) {

            technology =
                    etOtherTech.getText()
                            .toString()
                            .trim();

            if (technology.isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter your technology",
                        Toast.LENGTH_SHORT
                ).show();

                etOtherTech.requestFocus();
                return;
            }

        } else {

            technology =
                    technology == null
                            ? ""
                            : technology.trim();

            if (technology.isEmpty()) {

                Toast.makeText(
                        this,
                        "Select your tech stack",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }
        }

        preferences.edit()
                .putString(
                        "tech_stack",
                        technology
                )
                .putBoolean(
                        "onboarding_completed",
                        true
                )
                .apply();

        String email =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                ).getString(
                        "email",
                        ""
                );

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
                    preferences.getString(
                            "target_role",
                            ""
                    ),
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

            database.setProfileCompleted(
                    email,
                    true
            );
        }

        getSharedPreferences(
                "user_data",
                MODE_PRIVATE
        )
                .edit()
                .putBoolean(
                        "profileCompleted",
                        true
                )
                .apply();

        Intent intent =
                new Intent(
                        activity_onboarding3.this,
                        activity_dashboard.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        startActivity(intent);
        finish();
    }

    private void goBack() {

        Intent intent =
                new Intent(
                        activity_onboarding3.this,
                        activity_onboarding2.class
                );

        startActivity(intent);
        finish();
    }
}