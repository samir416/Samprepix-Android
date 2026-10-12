package com.example.samprepix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class activity_resume_result extends AppCompatActivity {

    private TextView btnBack;
    private TextView btnMenu;

    private TextView tvAtsScore;
    private TextView tvMatch;
    private TextView tvRole;

    private TextView tvProjects;
    private TextView tvEducation;
    private TextView tvGithub;
    private TextView tvExperience;

    private TextView tvKeywords;
    private TextView tvSuggestions;

    private ProgressBar atsProgress;
    private ProgressBar progressProjects;
    private ProgressBar progressEducation;
    private ProgressBar progressGithub;
    private ProgressBar progressExperience;

    private LinearLayout overviewSection;
    private LinearLayout keywordsSection;
    private LinearLayout suggestionsSection;
    private LinearLayout strengthsSection;

    private TextView tabOverview;
    private TextView tabKeywords;
    private TextView tabSuggestions;

    private Button btnDownloadReport;

    private String analysisResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resume_result);

        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);

        tvAtsScore = findViewById(R.id.tv_ats_score);
        tvMatch = findViewById(R.id.tv_match);
        tvRole = findViewById(R.id.tv_role);

        tvProjects = findViewById(R.id.tv_projects);
        tvEducation = findViewById(R.id.tv_education);
        tvGithub = findViewById(R.id.tv_github);
        tvExperience = findViewById(R.id.tv_experience);

        tvKeywords = findViewById(R.id.tv_keywords);
        tvSuggestions = findViewById(R.id.tv_suggestions);

        atsProgress = findViewById(R.id.ats_progress);
        progressProjects = findViewById(R.id.progress_content);
        progressEducation = findViewById(R.id.progress_structure);
        progressGithub = findViewById(R.id.progress_skills);
        progressExperience = findViewById(R.id.progress_experience);

        overviewSection = findViewById(R.id.overview_section);
        keywordsSection = findViewById(R.id.keywords_section);
        suggestionsSection = findViewById(R.id.suggestions_section);
        strengthsSection = findViewById(R.id.strengths_section);

        tabOverview = findViewById(R.id.tab_overview);
        tabKeywords = findViewById(R.id.tab_keywords);
        tabSuggestions = findViewById(R.id.tab_suggestions);

        btnDownloadReport =
                findViewById(R.id.btn_download_report);

        analysisResponse =
                getIntent().getStringExtra(
                        "analysis_response"
                );

        if (
                analysisResponse == null ||
                        analysisResponse.trim().isEmpty()
        ) {

            Toast.makeText(
                    this,
                    "Analysis result not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        showAnalysis();

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnMenu.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            activity_resume_result.this,
                            activity_dashboard.class
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });

        tabOverview.setOnClickListener(
                v -> showOverview()
        );

        tabKeywords.setOnClickListener(
                v -> showKeywords()
        );

        tabSuggestions.setOnClickListener(
                v -> showSuggestions()
        );

        btnDownloadReport.setOnClickListener(
                v -> shareReport()
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
    }

    private void showAnalysis() {

        try {

            JSONObject json =
                    new JSONObject(
                            analysisResponse
                    );

            boolean validResume =
                    json.optBoolean(
                            "validResume",
                            true
                    );

            if (!validResume) {

                tvAtsScore.setText("0");
                tvMatch.setText("Invalid Resume");
                tvRole.setText("Role: Not detected");

                atsProgress.setProgress(0);

                String rejectionReason =
                        json.optString(
                                "rejectionReason",
                                "The uploaded document was not detected as an IT resume."
                        );

                tvKeywords.setText(
                        rejectionReason
                );

                tvSuggestions.setText(
                        "Please upload a valid technical resume with clear education, project details, and skills."
                );

                showKeywords();

                return;
            }

            int score =
                    json.optInt(
                            "score",
                            0
                    );

            score =
                    Math.max(
                            0,
                            Math.min(
                                    100,
                                    score
                            )
                    );

            String role =
                    json.optString(
                            "role",
                            "Software Engineer"
                    );

            boolean githubFound =
                    json.optBoolean(
                            "githubFound",
                            false
                    );

            boolean linkedinFound =
                    json.optBoolean(
                            "linkedinFound",
                            false
                    );

            boolean projectFound =
                    json.optBoolean(
                            "projectFound",
                            false
                    );

            boolean educationFound =
                    json.optBoolean(
                            "educationFound",
                            false
                    );

            boolean experienceFound =
                    json.optBoolean(
                            "experienceFound",
                            false
                    );

            JSONArray skills =
                    json.optJSONArray(
                            "skills"
                    );

            JSONArray missingSkills =
                    json.optJSONArray(
                            "missingSkills"
                    );

            JSONArray suggestions =
                    json.optJSONArray(
                            "suggestions"
                    );

            tvAtsScore.setText(
                    String.valueOf(score)
            );

            atsProgress.setProgress(
                    score
            );

            String status;

            if (score >= 90) {
                status = "Excellent";
            } else if (score >= 75) {
                status = "Good";
            } else if (score >= 60) {
                status = "Average";
            } else {
                status = "Needs Improvement";
            }

            tvMatch.setText(
                    score + "% • " + status
            );

            tvRole.setText(
                    "Role: " + role
            );

            bindCheckItem(
                    tvProjects,
                    progressProjects,
                    projectFound
            );

            bindCheckItem(
                    tvEducation,
                    progressEducation,
                    educationFound
            );

            bindCheckItem(
                    tvGithub,
                    progressGithub,
                    githubFound || linkedinFound
            );

            bindCheckItem(
                    tvExperience,
                    progressExperience,
                    experienceFound
            );

            buildStrengths(
                    skills
            );

            buildSkills(
                    skills,
                    missingSkills
            );

            buildSuggestions(
                    suggestions
            );

            showOverview();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Error reading analysis result",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void buildStrengths(
            JSONArray skills
    ) {

        strengthsSection.removeAllViews();

        if (
                skills == null ||
                        skills.length() == 0
        ) {

            TextView empty =
                    createTextView(
                            "No detected skills found",
                            R.color.text_dark,
                            14
                    );

            strengthsSection.addView(
                    empty
            );

            return;
        }

        for (int i = 0; i < skills.length(); i++) {

            TextView skill =
                    createTextView(
                            "✓ " +
                                    skills.optString(i),
                            R.color.text,
                            14
                    );

            skill.setPadding(
                    0,
                    6,
                    0,
                    6
            );

            strengthsSection.addView(
                    skill
            );
        }
    }

    private void buildSkills(
            JSONArray skills,
            JSONArray missingSkills
    ) {

        StringBuilder builder =
                new StringBuilder();

        int detectedCount =
                skills == null
                        ? 0
                        : skills.length();

        builder.append(
                        "Detected Skills ("
                )
                .append(
                        detectedCount
                )
                .append(
                        "):\n\n"
                );

        if (
                skills != null &&
                        skills.length() > 0
        ) {

            for (
                    int i = 0;
                    i < skills.length();
                    i++
            ) {

                builder.append("• ")
                        .append(
                                skills.optString(i)
                        )
                        .append("\n");
            }

        } else {

            builder.append(
                    "None detected\n"
            );
        }

        int missingCount =
                missingSkills == null
                        ? 0
                        : missingSkills.length();

        builder.append(
                        "\nRecommended Missing Skills ("
                )
                .append(
                        missingCount
                )
                .append(
                        "):\n\n"
                );

        if (
                missingSkills != null &&
                        missingSkills.length() > 0
        ) {

            for (
                    int i = 0;
                    i < missingSkills.length();
                    i++
            ) {

                builder.append("• ")
                        .append(
                                missingSkills.optString(i)
                        )
                        .append("\n");
            }

        } else {

            builder.append(
                    "No critical skills missing for target role.\n"
            );
        }

        tvKeywords.setText(
                builder.toString().trim()
        );
    }

    private void buildSuggestions(
            JSONArray suggestions
    ) {

        StringBuilder builder =
                new StringBuilder();

        if (
                suggestions != null &&
                        suggestions.length() > 0
        ) {

            for (
                    int i = 0;
                    i < suggestions.length();
                    i++
            ) {

                builder.append(
                                i + 1
                        )
                        .append(". ")
                        .append(
                                suggestions.optString(i)
                        )
                        .append(
                                "\n\n"
                        );
            }

        } else {

            builder.append(
                    "No specific suggestions provided. Your resume meets baseline ATS requirements."
            );
        }

        tvSuggestions.setText(
                builder.toString().trim()
        );
    }

    private TextView createTextView(
            String text,
            int color,
            int size
    ) {

        TextView view =
                new TextView(this);

        view.setText(text);
        view.setTextColor(
                getColor(color)
        );
        view.setTextSize(size);

        return view;
    }

    private void bindCheckItem(
            TextView label,
            ProgressBar progressBar,
            boolean found
    ) {

        label.setText(
                found
                        ? "Found"
                        : "Missing"
        );

        label.setTextColor(
                getColor(
                        found
                                ? R.color.green
                                : R.color.red
                )
        );

        progressBar.setProgress(
                found
                        ? 100
                        : 0
        );
    }

    private void shareReport() {

        try {

            JSONObject json =
                    new JSONObject(
                            analysisResponse
                    );

            int score =
                    json.optInt(
                            "score",
                            0
                    );

            String role =
                    json.optString(
                            "role",
                            "Software Engineer"
                    );

            StringBuilder report =
                    new StringBuilder();

            report.append(
                    "Samprepix Resume Analysis\n\n"
            );

            report.append(
                            "Role: "
                    )
                    .append(role)
                    .append("\n");

            report.append(
                            "ATS Score: "
                    )
                    .append(score)
                    .append("/100\n\n");

            report.append(
                    "Analyzed with Samprepix AI Placement Platform."
            );

            Intent shareIntent =
                    new Intent(
                            Intent.ACTION_SEND
                    );

            shareIntent.setType(
                    "text/plain"
            );

            shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Samprepix Resume Analysis Report"
            );

            shareIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    report.toString()
            );

            startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Resume Report"
                    )
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to generate report",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void showOverview() {

        overviewSection.setVisibility(
                View.VISIBLE
        );

        keywordsSection.setVisibility(
                View.GONE
        );

        suggestionsSection.setVisibility(
                View.GONE
        );

        setTabState(
                tabOverview,
                true
        );

        setTabState(
                tabKeywords,
                false
        );

        setTabState(
                tabSuggestions,
                false
        );
    }

    private void showKeywords() {

        overviewSection.setVisibility(
                View.GONE
        );

        keywordsSection.setVisibility(
                View.VISIBLE
        );

        suggestionsSection.setVisibility(
                View.GONE
        );

        setTabState(
                tabOverview,
                false
        );

        setTabState(
                tabKeywords,
                true
        );

        setTabState(
                tabSuggestions,
                false
        );
    }

    private void showSuggestions() {

        overviewSection.setVisibility(
                View.GONE
        );

        keywordsSection.setVisibility(
                View.GONE
        );

        suggestionsSection.setVisibility(
                View.VISIBLE
        );

        setTabState(
                tabOverview,
                false
        );

        setTabState(
                tabKeywords,
                false
        );

        setTabState(
                tabSuggestions,
                true
        );
    }

    private void setTabState(
            TextView tab,
            boolean selected
    ) {

        tab.setBackgroundResource(
                selected
                        ? R.drawable.btn_bg
                        : R.drawable.card_bg
        );

        tab.setTextColor(
                getColor(
                        selected
                                ? R.color.text
                                : R.color.text_dark
                )
        );
    }
}