package com.example.samprepix;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_resume_result extends AppCompatActivity {

    TextView btnBack , btnMenu , tvAtsScore , tvMatch ;
    ProgressBar atsProgress , progressContent , progressStructure , progressSkills , progressExperience , tvContentScore ,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resume_result);

    }
}