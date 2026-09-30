package com.example.samprepix;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_onboarding3 extends AppCompatActivity {

    LinearLayout techJava,techSpring,techJavaScript,techReact,techNode,techMongoDb,techPython,techCpp,techOther;
    Button btnBack,btnFinish;
    SharedPreferences preferences;
    String selectedTech="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding3);

        techCpp = findViewById(R.id.tech_cpp);
        techJava = findViewById(R.id.tech_java);
        techSpring = findViewById(R.id.tech_spring);
        techNode = findViewById(R.id.tech_node);
        techJavaScript = findViewById(R.id.tech_javascript);
        techMongoDb = findViewById(R.id.tech_mongodb);
        techOther = findViewById(R.id.tech_other);
        techPython = findViewById(R.id.tech_python);
        techReact = findViewById(R.id.tech_react);

        btnBack = findViewById(R.id.btn_back);
        btnFinish = findViewById(R.id.btn_finish);

        preferences = getSharedPreferences("onboarding_data",MODE_PRIVATE);
        techJava.setOnClickListener(v->{
            selectTech(techJava,"Java");
        });
        techReact.setOnClickListener(v->{
            selectTech(techReact,"React");
        });

        techSpring.setOnClickListener(v -> {
            selectTech(techSpring, "Spring Boot");
        });

        techJavaScript.setOnClickListener(v -> {
            selectTech(techJavaScript, "JavaScript");
        });

        techNode.setOnClickListener((v->{
            selectTech(techNode,"techNode");
        }));

        techMongodb.setOnClickListener(v -> {
            selectTech(techMongodb, "MongoDB");
        });

        techPython.setOnClickListener(v -> {
            selectTech(techPython, "Python");
        });

        techCpp.setOnClickListener(v -> {
            selectTech(techCpp, "C++");
        });

        techOther.setOnClickListener(v -> {
            selectTech(techOther, "Other");
        });

        btnBack.setOnClickListener(v->{
            finish();
        });

        btnFinish.setOnClickListener(v->{
            if (selectedTech.isEmpty()) {
                Toast.makeText(this, "Select your tech stack", Toast.LENGTH_SHORT).show();
            }
            else{
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("tech_stack",selectedTech);
                editor.putBoolean("onboarding_completed",true);
                editor.apply();
                Toast.makeText(this, "Onboarding Completed", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void selectTech(LinearLayout selectedLayout , String techName)
    {
        ;
        techNode.setBackgroundResource(R.drawable.card_bg);
        techJava.setBackgroundResource(R.drawable.card_bg);
        techSpring.setBackgroundResource(R.drawable.card_bg);
        techReact.setBackgroundResource(R.drawable.card_bg);
        techJavaScript.setBackgroundResource(R.drawable.card_bg);
        techOther.setBackgroundResource(R.drawable.card_bg);
        techMongoDb.setBackgroundResource(R.drawable.card_bg);
        techCpp.setBackgroundResource(R.drawable.card_bg);
        techPython.setBackgroundResource(R.drawable.card_bg);

        selectedLayout.setBackgroundResource(
                R.drawable.btn_outline
        );

        selectedTech = techName;


    }
}