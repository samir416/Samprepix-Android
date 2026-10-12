package com.example.samprepix;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_splash extends AppCompatActivity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable openLogin = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(activity_splash.this, activity_login.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        handler.postDelayed(openLogin, 3000);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(openLogin);
        super.onDestroy();
    }
}