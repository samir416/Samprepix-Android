package com.example.samprepix;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    EditText etname , etpassword , etCnpassword , etemail;
    Button btnregister;
    LinearLayout btnGoogle , btnGithub;
    TextView tvlogin;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        etname = findViewById(R.id.et_name);
        etpassword = findViewById(R.id.et_password);
        etemail = findViewById(R.id.et_email);
        etCnpassword = findViewById(R.id.et_confirm_password);

        btnregister = findViewById(R.id.btn_register);
        btnGoogle = findViewById(R.id.btn_google);
        btnGithub =findViewById(R.id.btn_github);

        tvlogin = findViewById(R.id.tv_login);

        preferences = getSharedPreferences("user_data",MODE_PRIVATE);

        btnregister.setOnClickListener(v->{
            String name = etname.getText().toString().trim();
            String email = etemail.getText().toString().trim();
            String password = etpassword.getText().toString().trim();
            String ConfirmPassword = etCnpassword.getText().toString().trim();

            if(name.isEmpty())
            {
                Toast.makeText(this,"Enter Name ",Toast.LENGTH_SHORT).show();
                etname.requestFocus();
            }
            else if(email.isEmpty())
            {
                Toast.makeText(this,"Enter Email ",Toast.LENGTH_SHORT).show();
                etemail.requestFocus();
            } else if (password.isEmpty()) {
                Toast.makeText(this,"Enter Password ",Toast.LENGTH_SHORT).show();
                etpassword.requestFocus();
            } else if (ConfirmPassword.isEmpty()) {
                Toast.makeText(this,"Confirm Password ",Toast.LENGTH_SHORT).show();
                etCnpassword.requestFocus();
            } else if (!password.equals(ConfirmPassword)) {
                Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                etCnpassword.requestFocus();
            } else if (!email.contains("@")) {

                Toast.makeText(this,"Enter a valid email",Toast.LENGTH_SHORT).show();
                etemail.requestFocus();

            } else if (password.length() < 6) {
                Toast.makeText(this,  "Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                etpassword.requestFocus();
            } else{

                String oldEmail = preferences.getString("email",",");

                if(email.equals(oldEmail))
                {
                    Toast.makeText(this,"Email already registered",Toast.LENGTH_SHORT).show();
                    etemail.requestFocus();
                }
                else{
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("name",name);
                    editor.putString("email",email);
                    editor.putString("password",password);

                    editor.apply();

                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,activity_login.class));
                    finish();

                }
            }
        });

        btnGoogle.setOnClickListener(v->{
            Toast.makeText(RegisterActivity.this,"Google Registration",Toast.LENGTH_SHORT).show();
        });

        btnGithub.setOnClickListener(v->{
            Toast.makeText(RegisterActivity.this,"Github Registration",Toast.LENGTH_SHORT).show();


        });

        tvlogin.setOnClickListener(v->{
            startActivity(new Intent(RegisterActivity.this , activity_login.class));
            finish();
        });



    }
}