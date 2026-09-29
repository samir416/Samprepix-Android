package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class activity_forgot extends AppCompatActivity {

    EditText etemail, etnewpassword, etconfirmpassword;
    Button reset;
    TextView tvlogin, tvshowpassword;
    LinearLayout passwordsection;

    SharedPreferences preferences;

    boolean passwordvisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot);

        etemail = findViewById(R.id.et_email);
        reset = findViewById(R.id.btn_reset);
        tvlogin = findViewById(R.id.tv_login);
        etnewpassword = findViewById(R.id.et_new_password);
        etconfirmpassword = findViewById(R.id.et_confirm_password);
        tvshowpassword = findViewById(R.id.tv_show_password);
        passwordsection = findViewById(R.id.pass_section);

        preferences = getSharedPreferences("user_data", MODE_PRIVATE);

        reset.setOnClickListener(v -> {

            if (passwordsection.getVisibility() == LinearLayout.GONE) {

                String email = etemail.getText().toString().trim();
                String savedEmail = preferences.getString("email", "");

                if (email.isEmpty()) {

                    Toast.makeText(this,
                            "Enter Email",
                            Toast.LENGTH_SHORT).show();

                    etemail.requestFocus();

                } else if (!email.contains("@") || !email.contains(".")) {

                    Toast.makeText(this,
                            "Enter a valid email",
                            Toast.LENGTH_SHORT).show();

                    etemail.requestFocus();

                } else if (savedEmail.isEmpty()) {

                    Toast.makeText(this,
                            "No registered account found",
                            Toast.LENGTH_SHORT).show();

                    etemail.requestFocus();

                } else if (!email.equals(savedEmail)) {

                    Toast.makeText(this,
                            "Email not registered",
                            Toast.LENGTH_SHORT).show();

                    etemail.requestFocus();

                } else {

                    passwordsection.setVisibility(LinearLayout.VISIBLE);
                    reset.setText("Update Password");

                    Toast.makeText(this,
                            "Email verified",
                            Toast.LENGTH_SHORT).show();
                }

            } else {

                String newPass =
                        etnewpassword.getText().toString().trim();

                String Cnfpass =
                        etconfirmpassword.getText().toString().trim();

                if (newPass.isEmpty()) {

                    Toast.makeText(this,
                            "Enter New Password",
                            Toast.LENGTH_SHORT).show();

                    etnewpassword.requestFocus();

                } else if (newPass.length() < 6) {

                    Toast.makeText(this,
                            "Password must be at least 6 characters",
                            Toast.LENGTH_SHORT).show();

                    etnewpassword.requestFocus();

                } else if (Cnfpass.isEmpty()) {

                    Toast.makeText(this,
                            "Confirm Password",
                            Toast.LENGTH_SHORT).show();

                    etconfirmpassword.requestFocus();

                } else if (!newPass.equals(Cnfpass)) {

                    Toast.makeText(this,
                            "Passwords do not match",
                            Toast.LENGTH_SHORT).show();

                    etconfirmpassword.requestFocus();

                } else {

                    SharedPreferences.Editor editor =
                            preferences.edit();

                    editor.putString("password", newPass);
                    editor.apply();

                    Toast.makeText(this,
                            "Password updated successfully",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(
                            activity_forgot.this,
                            activity_login.class
                    ));

                    finish();
                }
            }
        });

        tvshowpassword.setOnClickListener(v -> {

            if (passwordvisible == false) {

                etnewpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                etconfirmpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                );

                tvshowpassword.setText("Hide");
                passwordvisible = true;

            } else {

                etnewpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                etconfirmpassword.setInputType(
                        InputType.TYPE_CLASS_TEXT |
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                );

                tvshowpassword.setText("Show");
                passwordvisible = false;
            }

            etnewpassword.setSelection(
                    etnewpassword.length()
            );

            etconfirmpassword.setSelection(
                    etconfirmpassword.length()
            );
        });

        tvlogin.setOnClickListener(v -> {

            startActivity(new Intent(
                    activity_forgot.this,
                    activity_login.class
            ));

            finish();
        });
    }
}