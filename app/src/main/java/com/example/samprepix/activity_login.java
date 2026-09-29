package com.example.samprepix;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class activity_login extends AppCompatActivity {

    EditText etemail,etpassword;
     CheckBox cbmember;
     Button btnlogin;
     LinearLayout btnGoogle , btnGithub;
     TextView tvforgot , tvregister;

     SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etemail = findViewById(R.id.et_email);
        etpassword = findViewById(R.id.et_password);
        cbmember = findViewById(R.id.cb_member);
        btnlogin= findViewById(R.id.btn_login);
        btnGithub = findViewById(R.id.btn_github);
        btnGoogle = findViewById(R.id.btn_google);
        tvforgot = findViewById(R.id.tv_forgot);
        tvregister = findViewById(R.id.tv_register);

        preferences = getSharedPreferences("login_data",MODE_PRIVATE);

        String email = preferences.getString("email","");
        boolean remember = preferences.getBoolean("remember",false);

          if (remember){

              etemail.setText(email);
              cbmember.setChecked(true);

          }

        btnlogin.setOnClickListener(v -> {


            String emailText = etemail.getText().toString().trim();
            String passwordText = etpassword.getText().toString().trim();

            if(emailText.isEmpty())
            {
                Toast.makeText(this,"Enter Email",Toast.LENGTH_SHORT).show();
                etemail.requestFocus();
            } else if (passwordText.isEmpty()) {
                Toast.makeText(this,"Enter password",Toast.LENGTH_SHORT).show();
                etpassword.requestFocus();

            }
            else{

                SharedPreferences.Editor editor = preferences.edit();

                if(cbmember.isChecked())
                {
                    editor.putString("email",emailText);
                    editor.putBoolean("remember",true);

                }
                else{
                    editor.clear();
                }
                editor.apply();

                Toast.makeText(activity_login.this, "Login Successful",Toast.LENGTH_SHORT).show();
            }
        });

        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(activity_login.this, "Google Login", Toast.LENGTH_SHORT).show();
        });

        btnGithub.setOnClickListener(v -> {
            Toast.makeText(activity_login.this, "GitHub Login", Toast.LENGTH_SHORT).show();
        });

        tvforgot.setOnClickListener(v -> {
            startActivity(new Intent(activity_login.this, activity_forgot.class
            ));
        });

        tvregister.setOnClickListener(v -> {
            startActivity(new Intent (activity_login.this, RegisterActivity.class));
        });
    }
}
