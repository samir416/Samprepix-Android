package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class activity_dashboard extends AppCompatActivity {

    private TextView btnMenu;
    private TextView btnCloseDrawer;
    private TextView btnSearch;

    private TextView tvGreeting;
    private TextView tvProfileInitial;
    private TextView tvProfileName;
    private TextView tvProfileEmail;

    private EditText etSearch;

    private View drawerOverlay;
    private ScrollView navigationScroll;

    private LinearLayout navigationDrawer;
    private LinearLayout navDashboard;
    private LinearLayout navResume;
    private LinearLayout navInterview;
    private LinearLayout navCoding;
    private LinearLayout navGithub;
    private LinearLayout navAnalytics;
    private LinearLayout navBilling;
    private LinearLayout navSettings;

    private LinearLayout cardResume;

    private TextView navLogout;

    private SharedPreferences preferences;
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        btnMenu = findViewById(R.id.btn_menu);
        btnSearch = findViewById(R.id.btn_search);
        btnCloseDrawer = findViewById(R.id.btn_close_drawer);

        tvGreeting = findViewById(R.id.tv_greeting);
        tvProfileInitial = findViewById(R.id.tv_profile_initial);
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);

        etSearch = findViewById(R.id.et_search);

        drawerOverlay = findViewById(R.id.drawer_overlay);
        navigationScroll = findViewById(R.id.navigation_scroll);
        navigationDrawer = findViewById(R.id.navigation_drawer);

        navDashboard = findViewById(R.id.nav_dashboard);
        navResume = findViewById(R.id.nav_resume);
        navInterview = findViewById(R.id.nav_interview);
        navCoding = findViewById(R.id.nav_coding);
        navGithub = findViewById(R.id.nav_github);
        navAnalytics = findViewById(R.id.nav_analytics);
        navBilling = findViewById(R.id.nav_billing);
        navSettings = findViewById(R.id.nav_settings);

        navLogout = findViewById(R.id.nav_logout);

        cardResume = findViewById(R.id.card_resume);

        preferences = getSharedPreferences(
                "user_data",
                MODE_PRIVATE
        );

        loginPreferences = getSharedPreferences(
                "login_data",
                MODE_PRIVATE
        );

        navigationScroll.setVisibility(View.GONE);
        drawerOverlay.setVisibility(View.GONE);
        etSearch.setVisibility(View.GONE);

        loadUserData();

        btnSearch.setOnClickListener(v -> {

            if (etSearch.getVisibility() == View.GONE) {

                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();

            } else {

                closeSearch();
            }
        });

        btnMenu.setOnClickListener(
                v -> openDrawer()
        );

        btnCloseDrawer.setOnClickListener(
                v -> closeDrawer()
        );

        drawerOverlay.setOnClickListener(
                v -> closeDrawer()
        );

        navDashboard.setOnClickListener(
                v -> closeDrawer()
        );

        navResume.setOnClickListener(v -> {

            closeDrawer();

            startActivity(
                    new Intent(
                            activity_dashboard.this,
                            activity_resume_analyzer.class
                    )
            );
        });

        cardResume.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            activity_dashboard.this,
                            activity_resume_analyzer.class
                    )
            );
        });

        navInterview.setOnClickListener(
                v -> closeDrawer()
        );

        navCoding.setOnClickListener(
                v -> closeDrawer()
        );

        navGithub.setOnClickListener(
                v -> closeDrawer()
        );

        navAnalytics.setOnClickListener(
                v -> closeDrawer()
        );

        navBilling.setOnClickListener(
                v -> closeDrawer()
        );

        navSettings.setOnClickListener(
                v -> closeDrawer()
        );

        navLogout.setOnClickListener(
                v -> logout()
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (navigationScroll.getVisibility()
                                == View.VISIBLE) {

                            closeDrawer();

                        } else if (etSearch.getVisibility()
                                == View.VISIBLE) {

                            closeSearch();

                        } else {

                            finish();
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {

        String name =
                preferences.getString(
                        "name",
                        ""
                );

        String email =
                preferences.getString(
                        "email",
                        ""
                );

        if (email.isEmpty()) {

            email =
                    loginPreferences.getString(
                            "email",
                            ""
                    );
        }

        if (name.isEmpty() && !email.isEmpty()) {

            String usernamePart =
                    email.contains("@")
                            ? email.substring(
                            0,
                            email.indexOf("@")
                    )
                            : email;

            String[] parts =
                    usernamePart.split("[._-]");

            StringBuilder nameBuilder =
                    new StringBuilder();

            for (String part : parts) {

                if (!part.isEmpty()) {

                    nameBuilder
                            .append(
                                    Character.toUpperCase(
                                            part.charAt(0)
                                    )
                            )
                            .append(
                                    part.substring(1)
                                            .toLowerCase()
                            )
                            .append(" ");
                }
            }

            name =
                    nameBuilder
                            .toString()
                            .trim();
        }

        if (name.isEmpty()) {
            name = "User";
        }

        tvGreeting.setText(
                "Good Morning,\n" +
                        name +
                        "! 👋"
        );

        tvProfileName.setText(name);

        tvProfileInitial.setText(
                name.substring(0, 1)
                        .toUpperCase()
        );

        tvProfileEmail.setText(email);
    }

    private void openDrawer() {

        navigationScroll.setVisibility(
                View.VISIBLE
        );

        drawerOverlay.setVisibility(
                View.VISIBLE
        );

        drawerOverlay.bringToFront();
        navigationScroll.bringToFront();
    }

    private void closeDrawer() {

        navigationScroll.setVisibility(
                View.GONE
        );

        drawerOverlay.setVisibility(
                View.GONE
        );
    }

    private void closeSearch() {

        etSearch.setVisibility(
                View.GONE
        );

        etSearch.setText("");
    }

    private void logout() {

        loginPreferences
                .edit()
                .clear()
                .apply();

        Intent intent =
                new Intent(
                        activity_dashboard.this,
                        activity_login.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK
        );

        startActivity(intent);
        finish();
    }
}