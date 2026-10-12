package com.example.samprepix;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText etname;
    private EditText etpassword;
    private EditText etCnpassword;
    private EditText etemail;

    private Button btnregister;

    private LinearLayout btnGoogle;
    private LinearLayout btnGithub;

    private TextView btnBack;
    private TextView tvlogin;

    private SharedPreferences preferences;
    private SharedPreferences onboardingPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.et_name);
        etpassword = findViewById(R.id.et_password);
        etemail = findViewById(R.id.et_email);
        etCnpassword =
                findViewById(R.id.et_confirm_password);

        btnregister =
                findViewById(R.id.btn_register);

        btnGoogle =
                findViewById(R.id.btn_google);

        btnGithub =
                findViewById(R.id.btn_github);

        btnBack =
                findViewById(R.id.btn_back);

        tvlogin =
                findViewById(R.id.tv_login);

        preferences =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                );

        onboardingPreferences =
                getSharedPreferences(
                        "onboarding_data",
                        MODE_PRIVATE
                );

        new activity_login.DatabaseHelper(this)
                .getWritableDatabase();

        btnBack.setOnClickListener(
                v -> goBackToLogin()
        );

        tvlogin.setOnClickListener(
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

        btnregister.setOnClickListener(
                v -> registerUser()
        );

        btnGoogle.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Google Sign-In will be available with cloud authentication.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnGithub.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "GitHub Sign-In will be available with cloud authentication.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    private void registerUser() {

        String name =
                etname.getText().toString().trim();

        String email =
                etemail.getText().toString().trim();

        String password =
                etpassword.getText().toString().trim();

        String confirmPassword =
                etCnpassword.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your full name",
                    Toast.LENGTH_SHORT
            ).show();

            etname.requestFocus();
            return;
        }

        if (name.length() < 2) {

            Toast.makeText(
                    this,
                    "Name must contain at least 2 characters",
                    Toast.LENGTH_SHORT
            ).show();

            etname.requestFocus();
            return;
        }

        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter your email address",
                    Toast.LENGTH_SHORT
            ).show();

            etemail.requestFocus();
            return;
        }

        if (!email.contains("@") ||
                !email.contains(".")) {

            Toast.makeText(
                    this,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT
            ).show();

            etemail.requestFocus();
            return;
        }

        if (password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please create a password",
                    Toast.LENGTH_SHORT
            ).show();

            etpassword.requestFocus();
            return;
        }

        if (password.length() < 6) {

            Toast.makeText(
                    this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT
            ).show();

            etpassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please confirm your password",
                    Toast.LENGTH_SHORT
            ).show();

            etCnpassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
            ).show();

            etCnpassword.requestFocus();
            return;
        }

        btnregister.setEnabled(false);
        btnregister.setText("Creating Account...");

        String baseUser =
                email
                        .split("@")[0]
                        .replaceAll(
                                "[^A-Za-z0-9_.-]",
                                ""
                        );

        if (baseUser.length() < 3) {
            baseUser = "user_" + baseUser;
        }

        if (baseUser.length() > 25) {
            baseUser =
                    baseUser.substring(0, 25);
        }

        final String username = baseUser;

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getRegisterUrl()
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

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

                request.put("name", name);
                request.put("username", username);
                request.put("email", email);
                request.put("password", password);

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

                if (responseCode == 200 ||
                        responseCode == 201) {

                    JSONObject regJson =
                            new JSONObject(
                                    responseText
                            );

                    String token = "";

                    if (regJson.has("token")) {

                        token =
                                cleanToken(
                                        regJson.getString(
                                                "token"
                                        )
                                );
                    }

                    final String finalToken =
                            token;

                    activity_login.DatabaseHelper database =
                            new activity_login.DatabaseHelper(
                                    RegisterActivity.this
                            );

                    database.saveUser(
                            name,
                            username,
                            email,
                            finalToken
                    );

                    runOnUiThread(() -> {

                        preferences.edit()
                                .putString(
                                        "name",
                                        name
                                )
                                .putString(
                                        "username",
                                        username
                                )
                                .putString(
                                        "email",
                                        email
                                )
                                .putString(
                                        "token",
                                        finalToken
                                )
                                .putBoolean(
                                        "profileCompleted",
                                        false
                                )
                                .apply();

                        if (!finalToken.isEmpty()) {

                            getSharedPreferences(
                                    "login_data",
                                    MODE_PRIVATE
                            )
                                    .edit()
                                    .putString(
                                            "token",
                                            finalToken
                                    )
                                    .putString(
                                            "email",
                                            email
                                    )
                                    .apply();
                        }

                        onboardingPreferences
                                .edit()
                                .clear()
                                .apply();

                        btnregister.setEnabled(true);
                        btnregister.setText("Register");

                        Toast.makeText(
                                RegisterActivity.this,
                                "Account created successfully. Please login.",
                                Toast.LENGTH_LONG
                        ).show();

                        startActivity(
                                new Intent(
                                        RegisterActivity.this,
                                        activity_login.class
                                )
                        );

                        finish();
                    });

                } else {

                    String errorMsg =
                            "We couldn't create your account. Please try again.";

                    try {

                        if (
                                responseText != null &&
                                        responseText
                                                .trim()
                                                .startsWith("{")
                        ) {

                            JSONObject errorJson =
                                    new JSONObject(
                                            responseText
                                    );

                            if (errorJson.has("message")) {

                                errorMsg =
                                        errorJson.getString(
                                                "message"
                                        );

                            } else if (
                                    errorJson.has("error")
                            ) {

                                errorMsg =
                                        errorJson.getString(
                                                "error"
                                        );
                            }

                        } else if (
                                responseText != null &&
                                        !responseText
                                                .trim()
                                                .isEmpty()
                        ) {

                            errorMsg =
                                    responseText.trim();
                        }

                    } catch (Exception ignored) {
                    }

                    final String displayError =
                            errorMsg;

                    runOnUiThread(() -> {

                        btnregister.setEnabled(true);
                        btnregister.setText("Register");

                        Toast.makeText(
                                RegisterActivity.this,
                                displayError,
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnregister.setEnabled(true);
                    btnregister.setText("Register");

                    String errorMsg =
                            e instanceof java.net.SocketTimeoutException
                                    ? "The request took too long. Please try again."
                                    : "Unable to connect right now. Please check your connection.";

                    Toast.makeText(
                            RegisterActivity.this,
                            errorMsg,
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

    private String cleanToken(String token) {

        if (token == null) {
            return "";
        }

        String cleaned =
                token.trim();

        while (
                cleaned.startsWith("\"") &&
                        cleaned.endsWith("\"") &&
                        cleaned.length() >= 2
        ) {

            cleaned =
                    cleaned.substring(
                            1,
                            cleaned.length() - 1
                    ).trim();
        }

        while (
                cleaned
                        .toLowerCase()
                        .startsWith("bearer ")
        ) {

            cleaned =
                    cleaned.substring(7).trim();
        }

        return cleaned;
    }

    private void goBackToLogin() {

        Intent intent =
                new Intent(
                        RegisterActivity.this,
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