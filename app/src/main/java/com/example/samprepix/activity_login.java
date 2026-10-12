package com.example.samprepix;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class activity_login extends AppCompatActivity {

    private EditText etemail;
    private EditText etpassword;
    private CheckBox cbmember;
    private Button btnlogin;
    private LinearLayout btnGoogle;
    private LinearLayout btnGithub;
    private TextView btnBack;
    private TextView tvforgot;
    private TextView tvregister;

    private SharedPreferences loginPreferences;
    private SharedPreferences userPreferences;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "samprepix.db";
        private static final int DATABASE_VERSION = 1;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(
                    "CREATE TABLE users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT," +
                            "username TEXT," +
                            "email TEXT UNIQUE NOT NULL," +
                            "token TEXT," +
                            "journey_type TEXT," +
                            "target_role TEXT," +
                            "experience_level TEXT," +
                            "current_role TEXT," +
                            "designation TEXT," +
                            "years_of_experience INTEGER DEFAULT 0," +
                            "career_goal TEXT," +
                            "profile_completed INTEGER DEFAULT 0," +
                            "auth_provider TEXT DEFAULT 'EMAIL'," +
                            "created_at INTEGER DEFAULT (strftime('%s','now'))" +
                            ")"
            );
        }

        @Override
        public void onUpgrade(
                SQLiteDatabase db,
                int oldVersion,
                int newVersion
        ) {
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }

        public void saveUser(
                String name,
                String username,
                String email,
                String token
        ) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("username", username);
            values.put("email", email);
            values.put("token", token);

            Cursor cursor = db.query(
                    "users",
                    new String[]{"id"},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            boolean exists = cursor.moveToFirst();
            cursor.close();

            if (exists) {
                db.update(
                        "users",
                        values,
                        "email = ?",
                        new String[]{email}
                );
            } else {
                db.insert("users", null, values);
            }
        }

        public void saveFullUser(
                String name,
                String username,
                String email,
                String token,
                String journeyType,
                String targetRole,
                String experienceLevel,
                String currentRole,
                String designation,
                int yearsOfExperience,
                String careerGoal,
                boolean profileCompleted
        ) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("name", name);
            values.put("username", username);
            values.put("email", email);
            values.put("token", token);
            values.put("journey_type", journeyType);
            values.put("target_role", targetRole);
            values.put("experience_level", experienceLevel);
            values.put("current_role", currentRole);
            values.put("designation", designation);
            values.put(
                    "years_of_experience",
                    yearsOfExperience
            );
            values.put("career_goal", careerGoal);
            values.put(
                    "profile_completed",
                    profileCompleted ? 1 : 0
            );

            Cursor cursor = db.query(
                    "users",
                    new String[]{"id"},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            boolean exists = cursor.moveToFirst();
            cursor.close();

            if (exists) {
                db.update(
                        "users",
                        values,
                        "email = ?",
                        new String[]{email}
                );
            } else {
                db.insert("users", null, values);
            }
        }

        public void updateProfile(
                String email,
                String journeyType,
                String targetRole,
                String experienceLevel,
                String currentRole,
                String designation,
                int yearsOfExperience,
                String careerGoal
        ) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("journey_type", journeyType);
            values.put("target_role", targetRole);
            values.put("experience_level", experienceLevel);
            values.put("current_role", currentRole);
            values.put("designation", designation);
            values.put(
                    "years_of_experience",
                    yearsOfExperience
            );
            values.put("career_goal", careerGoal);

            db.update(
                    "users",
                    values,
                    "email = ?",
                    new String[]{email}
            );
        }

        public void setProfileCompleted(
                String email,
                boolean completed
        ) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(
                    "profile_completed",
                    completed ? 1 : 0
            );

            db.update(
                    "users",
                    values,
                    "email = ?",
                    new String[]{email}
            );
        }

        public String getString(
                String email,
                String column
        ) {

            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(
                    "users",
                    new String[]{column},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            String value = "";

            if (cursor.moveToFirst()) {

                int index =
                        cursor.getColumnIndex(column);

                if (index >= 0 && !cursor.isNull(index)) {
                    value = cursor.getString(index);
                }
            }

            cursor.close();

            return value;
        }

        public boolean getBoolean(
                String email,
                String column
        ) {

            return getInt(email, column) == 1;
        }

        public int getInt(
                String email,
                String column
        ) {

            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.query(
                    "users",
                    new String[]{column},
                    "email = ?",
                    new String[]{email},
                    null,
                    null,
                    null
            );

            int value = 0;

            if (cursor.moveToFirst()) {

                int index =
                        cursor.getColumnIndex(column);

                if (index >= 0 && !cursor.isNull(index)) {
                    value = cursor.getInt(index);
                }
            }

            cursor.close();

            return value;
        }

        public void updateToken(
                String email,
                String token
        ) {

            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put("token", token);

            db.update(
                    "users",
                    values,
                    "email = ?",
                    new String[]{email}
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        new DatabaseHelper(this).getWritableDatabase();

        etemail = findViewById(R.id.et_email);
        etpassword = findViewById(R.id.et_password);
        cbmember = findViewById(R.id.cb_member);

        btnlogin = findViewById(R.id.btn_login);
        btnGoogle = findViewById(R.id.btn_google);
        btnGithub = findViewById(R.id.btn_github);
        btnBack = findViewById(R.id.btn_back);

        tvforgot = findViewById(R.id.tv_forgot);
        tvregister = findViewById(R.id.tv_register);

        loginPreferences =
                getSharedPreferences(
                        "login_data",
                        MODE_PRIVATE
                );

        userPreferences =
                getSharedPreferences(
                        "user_data",
                        MODE_PRIVATE
                );

        loadRememberedEmail();

        btnBack.setOnClickListener(v -> exitApp());

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        exitApp();
                    }
                }
        );

        btnlogin.setOnClickListener(v -> loginUser());

        btnGoogle.setOnClickListener(v ->
                Toast.makeText(
                        activity_login.this,
                        "Google Sign-In is not available in local mode.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnGithub.setOnClickListener(v ->
                Toast.makeText(
                        activity_login.this,
                        "GitHub Sign-In is not available in local mode.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        tvforgot.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                activity_login.this,
                                activity_forgot.class
                        )
                )
        );

        tvregister.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                activity_login.this,
                                RegisterActivity.class
                        )
                )
        );
    }

    private void loadRememberedEmail() {

        String savedEmail =
                loginPreferences.getString(
                        "email",
                        ""
                );

        boolean remember =
                loginPreferences.getBoolean(
                        "remember",
                        false
                );

        if (remember && !savedEmail.isEmpty()) {
            etemail.setText(savedEmail);
            cbmember.setChecked(true);
        }
    }

    private void loginUser() {

        String email =
                etemail.getText().toString().trim();

        String password =
                etpassword.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(
                    this,
                    "Please enter your email address",
                    Toast.LENGTH_SHORT
            ).show();
            etemail.requestFocus();
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
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
                    "Please enter your password",
                    Toast.LENGTH_SHORT
            ).show();
            etpassword.requestFocus();
            return;
        }

        setLoginLoading(true);

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getLoginUrl()
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

                if (responseCode >= 200 &&
                        responseCode < 300) {

                    JSONObject json =
                            new JSONObject(responseText);

                    if (!json.has("token")) {
                        showLoginError(
                                "Authentication could not be completed."
                        );
                        return;
                    }

                    String token =
                            cleanToken(
                                    json.getString("token")
                            );

                    if (token.isEmpty()) {
                        showLoginError(
                                "Authentication could not be completed."
                        );
                        return;
                    }

                    final String cleanToken = token;

                    runOnUiThread(() -> {

                        saveLoginSession(
                                email,
                                cleanToken
                        );

                        fetchCurrentUser(
                                cleanToken,
                                email
                        );
                    });

                } else {

                    showLoginError(
                            getServerErrorMessage(
                                    responseText,
                                    responseCode
                            )
                    );
                }

            } catch (Exception e) {

                showLoginError(
                        e instanceof java.net.SocketTimeoutException
                                ? "The login request timed out. Please try again."
                                : "Unable to connect right now. Please check your connection."
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private void fetchCurrentUser(
            String token,
            String email
    ) {

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getCurrentUserUrl()
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setDoInput(true);
                connection.setUseCaches(false);

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

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

                    JSONObject user =
                            new JSONObject(responseText);

                    boolean profileCompleted =
                            user.optBoolean(
                                    "profileCompleted",
                                    false
                            );

                    String name =
                            user.optString(
                                    "name",
                                    ""
                            );

                    String username =
                            user.optString(
                                    "username",
                                    ""
                            );

                    if (username.isEmpty()) {
                        username =
                                user.optString(
                                        "userName",
                                        ""
                                );
                    }

                    String journeyType =
                            user.optString(
                                    "journeyType",
                                    ""
                            );

                    String targetRole =
                            user.optString(
                                    "targetRole",
                                    ""
                            );

                    String experienceLevel =
                            user.optString(
                                    "experienceLevel",
                                    ""
                            );

                    String currentRole =
                            user.optString(
                                    "currentRole",
                                    ""
                            );

                    String designation =
                            user.optString(
                                    "designation",
                                    ""
                            );

                    int yearsOfExperience =
                            user.optInt(
                                    "yearsOfExperience",
                                    0
                            );

                    String careerGoal =
                            user.optString(
                                    "careerGoal",
                                    ""
                            );

                    DatabaseHelper database =
                            new DatabaseHelper(
                                    activity_login.this
                            );

                    database.saveFullUser(
                            name,
                            username,
                            email,
                            token,
                            journeyType,
                            targetRole,
                            experienceLevel,
                            currentRole,
                            designation,
                            yearsOfExperience,
                            careerGoal,
                            profileCompleted
                    );

                    runOnUiThread(() -> {

                        userPreferences.edit()
                                .putString("email", email)
                                .putString("token", token)
                                .putString(
                                        "user",
                                        user.toString()
                                )
                                .putBoolean(
                                        "profileCompleted",
                                        profileCompleted
                                )
                                .apply();

                        setLoginLoading(false);

                        Intent intent;

                        if (profileCompleted) {

                            intent =
                                    new Intent(
                                            activity_login.this,
                                            activity_dashboard.class
                                    );

                        } else {

                            intent =
                                    new Intent(
                                            activity_login.this,
                                            activity_onboarding1.class
                                    );
                        }

                        startActivity(intent);
                        finish();
                    });

                } else if (responseCode == 401) {

                    clearInvalidSession();

                    showLoginError(
                            "Your session could not be verified. Please sign in again."
                    );

                } else {

                    showLoginError(
                            "Unable to load your account. Please try again."
                    );
                }

            } catch (Exception e) {

                showLoginError(
                        "Unable to load your account. Please try again."
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private void saveLoginSession(
            String email,
            String token
    ) {

        SharedPreferences.Editor editor =
                loginPreferences.edit();

        if (cbmember.isChecked()) {

            editor.putString(
                    "email",
                    email
            );

            editor.putBoolean(
                    "remember",
                    true
            );

        } else {

            editor.remove("email");
            editor.remove("remember");
        }

        editor.putString(
                "token",
                token
        );

        editor.apply();

        userPreferences.edit()
                .putString("email", email)
                .putString("token", token)
                .apply();

        DatabaseHelper database =
                new DatabaseHelper(this);

        database.saveUser(
                "",
                "",
                email,
                token
        );
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

    private String getServerErrorMessage(
            String responseText,
            int responseCode
    ) {

        String message =
                "Email or password is incorrect";

        try {

            if (
                    responseText != null &&
                            responseText.trim()
                                    .startsWith("{")
            ) {

                JSONObject error =
                        new JSONObject(responseText);

                if (error.has("message")) {
                    message =
                            error.getString("message");
                } else if (error.has("error")) {
                    message =
                            error.getString("error");
                }

            } else if (
                    responseText != null &&
                            !responseText.trim().isEmpty()
            ) {

                message =
                        responseText.trim();
            }

        } catch (Exception ignored) {
        }

        return message;
    }

    private void setLoginLoading(
            boolean loading
    ) {

        runOnUiThread(() -> {

            btnlogin.setEnabled(!loading);

            btnlogin.setText(
                    loading
                            ? "Signing in..."
                            : "Login"
            );

            btnGoogle.setEnabled(!loading);
            btnGithub.setEnabled(!loading);
            tvforgot.setEnabled(!loading);
            tvregister.setEnabled(!loading);
            btnBack.setEnabled(!loading);
        });
    }

    private void showLoginError(
            String message
    ) {

        runOnUiThread(() -> {

            setLoginLoading(false);

            Toast.makeText(
                    activity_login.this,
                    message,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void clearInvalidSession() {

        loginPreferences.edit()
                .remove("token")
                .apply();

        userPreferences.edit()
                .remove("token")
                .remove("user")
                .remove("profileCompleted")
                .apply();

        DatabaseHelper database =
                new DatabaseHelper(this);

        String email =
                userPreferences.getString(
                        "email",
                        ""
                );

        if (!email.isEmpty()) {
            database.updateToken(email, "");
        }
    }

    private void exitApp() {
        finishAndRemoveTask();
    }
}