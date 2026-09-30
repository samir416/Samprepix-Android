package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_dashboard extends AppCompatActivity {

    TextView btnMenu,btnCloseDrawer,tvGreeting,tvProfileInitial,tvProfileName,tvProfileEmail;
    View drawerOverlay;
    ScrollView navigationScroll;

    LinearLayout navigationDrawer,navDashboard,navResume,navInterview,navCoding,navGithub,navAnalytics,navBilling,navSettings;
    TextView navLogout;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        btnMenu = findViewById(R.id.btn_menu);
        btnCloseDrawer = findViewById(R.id.btn_close_drawer);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvProfileInitial = findViewById(R.id.tv_profile_initial);
        tvProfileName = findViewById(R.id.tv_profile_name);
        tvProfileEmail = findViewById(R.id.tv_profile_email);

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
        navigationScroll = findViewById(R.id.navigation_scroll);
        navigationDrawer = findViewById(R.id.navigation_drawer);

        preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        loadUserData();
        btnMenu.setOnClickListener(v->{
            openDrawer();
        });
        btnCloseDrawer.setOnClickListener(v->{
            closeDrawer();
        });
        drawerOverlay.setOnClickListener(v -> {
            closeDrawer();
        });
        navDashboard.setOnClickListener(v -> {
            closeDrawer();
        });
        navResume.setOnClickListener(v -> {
            closeDrawer();
        });
        navInterview.setOnClickListener(v -> {
            closeDrawer();
        });

        navCoding.setOnClickListener(v -> {
            closeDrawer();
        });

        navGithub.setOnClickListener(v -> {
            closeDrawer();
        });

        navAnalytics.setOnClickListener(v -> {
            closeDrawer();
        });

        navBilling.setOnClickListener(v -> {
            closeDrawer();
        });

        navSettings.setOnClickListener(v -> {
            closeDrawer();
        });

        navLogout.setOnClickListener(v ->{
            Intent intent = new Intent(activity_dashboard.this,activity_login.class);
            startActivity(intent);
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {

            @Override
            public void handleOnBackPressed() {

                if (drawerOverlay.getVisibility() == View.VISIBLE) {
                    closeDrawer();
                } else {
                    finish();
                }
            }
        });
    }
    private void loadUserData(){
        String name = preferences.getString("name","");
        String email = preferences.getString("email","");
        if (!name.isEmpty()) {
            tvGreeting.setText("Good Morning,\n"+name+"! 👋");
            tvProfileName.setText(name);
            String firstLetter = name.substring(0,1).toUpperCase();
            tvProfileInitial.setText(firstLetter);

        }
        else {

            tvGreeting.setText("Good Morning");
            tvProfileName.setText("");
            tvProfileInitial.setText("");
        }

        tvProfileEmail.setText(email);

    }
    private void openDrawer() {

        drawerOverlay.setVisibility(View.VISIBLE);

        navigationScroll.setTranslationX(0);
    }
    private void closeDrawer() {

        navigationScroll.setTranslationX(-320);

        drawerOverlay.setVisibility(View.GONE);
    }


    }
