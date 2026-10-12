package com.example.samprepix;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class activity_forgot extends AppCompatActivity {

    private EditText etEmail;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnReset;
    private TextView btnBack;
    private TextView tvShowPassword;

    private LinearLayout passSection;

    private SharedPreferences userPreferences;

    private boolean passwordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);

        etEmail =
                findViewById(R.id.et_email);

        etNewPassword =
                findViewById(R.id.et_new_password);

        etConfirmPassword =
                findViewById(R.id.et_confirm_password);

        btnReset =
                findViewById(R.id.btn_reset);

        btnBack =
                findViewById(R.id.btn_back);

        tvShowPassword =
                findViewById(R.id.tv_show_password);

        passSection =
                findViewById(R.id.pass_section);

        userPreferences =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                );

        String savedEmail =
                userPreferences.getString(
                        "email",
                        ""
                );

        if (!savedEmail.isEmpty()) {
            etEmail.setText(savedEmail);
        }

        btnBack.setOnClickListener(
                v -> finish()
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                }
        );

        tvShowPassword.setOnClickListener(
                v -> togglePasswordVisibility()
        );

        btnReset.setOnClickListener(
                v -> requestPasswordReset()
        );
    }

    private void requestPasswordReset() {

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your email address",
                    Toast.LENGTH_SHORT
            ).show();

            etEmail.requestFocus();
            return;
        }

        if (!email.contains("@") ||
                !email.contains(".")) {

            Toast.makeText(
                    this,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
            ).show();

            etEmail.requestFocus();
            return;
        }

        btnReset.setEnabled(false);
        btnReset.setText("Sending...");

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getBaseUrl()
                                        + "/forgot-password"
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                JSONObject request =
                        new JSONObject();

                request.put(
                        "email",
                        email
                );

                OutputStream outputStream =
                        connection.getOutputStream();

                outputStream.write(
                        request.toString()
                                .getBytes("UTF-8")
                );

                outputStream.flush();
                outputStream.close();

                int responseCode =
                        connection.getResponseCode();

                InputStream inputStream =
                        responseCode >= 200 &&
                                responseCode < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                StringBuilder response =
                        new StringBuilder();

                if (inputStream != null) {

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            inputStream,
                                            "UTF-8"
                                    )
                            );

                    String line;

                    while (
                            (line = reader.readLine())
                                    != null
                    ) {
                        response.append(line);
                    }

                    reader.close();
                }

                String responseText =
                        response.toString();

                if (responseCode >= 200 &&
                        responseCode < 300) {

                    runOnUiThread(() -> {

                        btnReset.setEnabled(true);
                        btnReset.setText("Reset Password");

                        Toast.makeText(
                                activity_forgot.this,
                                "Password reset instructions have been sent to your email.",
                                Toast.LENGTH_LONG
                        ).show();

                        passSection.setVisibility(
                                LinearLayout.GONE
                        );
                    });

                } else {

                    String message =
                            "Unable to send password reset request.";

                    try {

                        if (
                                responseText != null &&
                                        responseText
                                                .trim()
                                                .startsWith("{")
                        ) {

                            JSONObject json =
                                    new JSONObject(
                                            responseText
                                    );

                            if (json.has("message")) {

                                message =
                                        json.getString(
                                                "message"
                                        );

                            } else if (
                                    json.has("error")
                            ) {

                                message =
                                        json.getString(
                                                "error"
                                        );
                            }
                        }

                    } catch (Exception ignored) {
                    }

                    final String finalMessage =
                            message;

                    runOnUiThread(() -> {

                        btnReset.setEnabled(true);
                        btnReset.setText("Reset Password");

                        Toast.makeText(
                                activity_forgot.this,
                                finalMessage,
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnReset.setEnabled(true);
                    btnReset.setText("Reset Password");

                    Toast.makeText(
                            activity_forgot.this,
                            "Unable to connect right now. Please try again.",
                            Toast.LENGTH_LONG
                    ).show();
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private void togglePasswordVisibility() {

        passwordVisible =
                !passwordVisible;

        int type =
                passwordVisible
                        ? android.text.InputType.TYPE_CLASS_TEXT |
                          android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        : android.text.InputType.TYPE_CLASS_TEXT |
                          android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

        etNewPassword.setInputType(type);
        etConfirmPassword.setInputType(type);

        etNewPassword.setSelection(
                etNewPassword.length()
        );

        etConfirmPassword.setSelection(
                etConfirmPassword.length()
        );

        tvShowPassword.setText(
                passwordVisible
                        ? "Hide"
                        : "Show"
        );
    }
}