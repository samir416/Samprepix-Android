package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class activity_resume_analyzer extends AppCompatActivity {

    private Button btnChooseFile;
    private Button btnAnalyze;

    private LinearLayout recentAnalyses;

    private TextView tvNoRecent;
    private TextView tvSelectedFile;
    private TextView tvUploadStatus;

    private ProgressBar progressAnalyze;

    private SharedPreferences loginPreferences;

    private ActivityResultLauncher<Intent> filePicker;

    private Uri selectedFileUri;
    private String selectedFileName = "";
    private long selectedFileSize = 0;

    private boolean isAnalyzing = false;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resume_analyzer);

        btnChooseFile = findViewById(R.id.btn_choose_file);
        btnAnalyze = findViewById(R.id.btn_analyze);

        recentAnalyses = findViewById(R.id.recent_analyses);

        tvNoRecent = findViewById(R.id.tv_no_recent);
        tvSelectedFile = findViewById(R.id.tv_selected_file);
        tvUploadStatus = findViewById(R.id.tv_upload_status);

        progressAnalyze = findViewById(R.id.progress_analyze);

        loginPreferences =
                getSharedPreferences(
                        "login_data",
                        MODE_PRIVATE
                );

        btnAnalyze.setVisibility(View.GONE);
        progressAnalyze.setVisibility(View.GONE);

        filePicker =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {

                            if (result.getResultCode() == RESULT_OK
                                    && result.getData() != null) {

                                Uri uri =
                                        result.getData().getData();

                                if (uri != null) {
                                    handleFileSelection(uri);
                                }
                            }
                        }
                );

        btnChooseFile.setOnClickListener(
                v -> openFilePicker()
        );

        btnAnalyze.setOnClickListener(v -> {

            if (isAnalyzing) {
                return;
            }

            if (selectedFileUri == null) {

                Toast.makeText(
                        this,
                        "Select a resume first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            analyzeResume();
        });

        TextView btnBack =
                findViewById(R.id.btn_back);

        if (btnBack != null) {

            btnBack.setOnClickListener(
                    v -> finish()
            );
        }

        TextView btnMenu =
                findViewById(R.id.btn_menu);

        if (btnMenu != null) {

            btnMenu.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                activity_resume_analyzer.this,
                                activity_dashboard.class
                        );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                );

                startActivity(intent);
                finish();
            });
        }

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        if (isAnalyzing) {

                            Toast.makeText(
                                    activity_resume_analyzer.this,
                                    "Analysis in progress...",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        finish();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentAnalyses();
    }

    private void openFilePicker() {

        Intent intent =
                new Intent(
                        Intent.ACTION_OPEN_DOCUMENT
                );

        intent.addCategory(
                Intent.CATEGORY_OPENABLE
        );

        intent.setType("*/*");

        String[] mimeTypes = {
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };

        intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                mimeTypes
        );

        filePicker.launch(intent);
    }

    private void handleFileSelection(Uri uri) {

        String fileName =
                getFileNameFromUri(uri);

        long fileSize =
                getFileSizeFromUri(uri);

        String lowerName =
                fileName.toLowerCase(Locale.US);

        boolean isSupported =
                lowerName.endsWith(".pdf")
                        || lowerName.endsWith(".docx")
                        || lowerName.endsWith(".doc");

        if (!isSupported) {

            Toast.makeText(
                    this,
                    "Unsupported file format. Please select PDF or DOCX.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (lowerName.endsWith(".doc")) {

            Toast.makeText(
                    this,
                    "DOC format not supported by backend. Please use PDF or DOCX.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        if (fileSize > MAX_FILE_SIZE) {

            Toast.makeText(
                    this,
                    "File size exceeds 5MB limit. Please choose a smaller file.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        selectedFileUri = uri;
        selectedFileName = fileName;
        selectedFileSize = fileSize;

        tvSelectedFile.setText(
                selectedFileName
        );

        tvSelectedFile.setVisibility(
                View.VISIBLE
        );

        String sizeText =
                formatFileSize(selectedFileSize);

        tvUploadStatus.setText(
                "Resume selected (" +
                        sizeText +
                        ")"
        );

        btnAnalyze.setVisibility(
                View.VISIBLE
        );

        btnAnalyze.setEnabled(true);
        btnAnalyze.setText("Analyze Resume");
    }

    private String getCleanToken() {

        String token =
                loginPreferences.getString(
                        "token",
                        ""
                );

        if (token == null || token.trim().isEmpty()) {

            SharedPreferences userPrefs =
                    getSharedPreferences(
                            "user_data",
                            MODE_PRIVATE
                    );

            token =
                    userPrefs.getString(
                            "token",
                            ""
                    );
        }

        if (token == null) {
            return "";
        }

        token = token.trim();

        while (
                token.startsWith("\"")
                        && token.endsWith("\"")
                        && token.length() >= 2
        ) {

            token =
                    token.substring(
                            1,
                            token.length() - 1
                    ).trim();
        }

        while (
                token.toLowerCase(Locale.US)
                        .startsWith("bearer ")
        ) {

            token =
                    token.substring(7)
                            .trim();
        }

        return token;
    }

    private void analyzeResume() {

        String token =
                getCleanToken();

        if (token.isEmpty()) {

            Toast.makeText(
                    this,
                    "Session expired. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent =
                    new Intent(
                            activity_resume_analyzer.this,
                            activity_login.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();

            return;
        }

        setLoading(true);

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getResumeAnalyzeUrl()
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(20000);
                connection.setReadTimeout(90000);
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

                String boundary =
                        "----SamprepixBoundary"
                                + System.currentTimeMillis();

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

                connection.setRequestProperty(
                        "Content-Type",
                        "multipart/form-data; boundary="
                                + boundary
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                String mimeType =
                        getFileMimeType(
                                selectedFileName,
                                selectedFileUri
                        );

                DataOutputStream outputStream =
                        new DataOutputStream(
                                connection.getOutputStream()
                        );

                outputStream.writeBytes(
                        "--" + boundary + "\r\n"
                );

                outputStream.writeBytes(
                        "Content-Disposition: form-data; " +
                                "name=\"file\"; filename=\"" +
                                selectedFileName +
                                "\"\r\n"
                );

                outputStream.writeBytes(
                        "Content-Type: " +
                                mimeType +
                                "\r\n\r\n"
                );

                InputStream fileInputStream =
                        getContentResolver()
                                .openInputStream(
                                        selectedFileUri
                                );

                if (fileInputStream == null) {
                    throw new Exception(
                            "Unable to open selected file"
                    );
                }

                byte[] buffer =
                        new byte[8192];

                int bytesRead;

                while (
                        (bytesRead =
                                fileInputStream.read(buffer))
                                != -1
                ) {

                    outputStream.write(
                            buffer,
                            0,
                            bytesRead
                    );
                }

                fileInputStream.close();

                outputStream.writeBytes(
                        "\r\n--" +
                                boundary +
                                "--\r\n"
                );

                outputStream.flush();
                outputStream.close();

                int responseCode =
                        connection.getResponseCode();

                InputStream responseStream =
                        (responseCode >= 200
                                && responseCode < 300)
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                StringBuilder response =
                        new StringBuilder();

                if (responseStream != null) {

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            responseStream,
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

                if (
                        responseCode >= 200
                                && responseCode < 300
                ) {

                    JSONObject jsonResponse =
                            new JSONObject(
                                    responseText
                            );

                    boolean validResume =
                            jsonResponse.optBoolean(
                                    "validResume",
                                    true
                            );

                    if (!validResume) {

                        String rejectionReason =
                                jsonResponse.optString(
                                        "rejectionReason",
                                        "The uploaded document was not detected as an IT resume."
                                );

                        runOnUiThread(() -> {

                            setLoading(false);

                            tvUploadStatus.setText(
                                    "Resume Rejected"
                            );

                            Toast.makeText(
                                    activity_resume_analyzer.this,
                                    rejectionReason,
                                    Toast.LENGTH_LONG
                            ).show();
                        });

                        return;
                    }

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Analysis completed"
                        );

                        Intent intent =
                                new Intent(
                                        activity_resume_analyzer.this,
                                        activity_resume_result.class
                                );

                        intent.putExtra(
                                "analysis_response",
                                responseText
                        );

                        startActivity(intent);
                    });

                } else if (responseCode == 400) {

                    String errorMsg =
                            "Invalid resume format or content";

                    try {

                        JSONObject errorJson =
                                new JSONObject(
                                        responseText
                                );

                        if (errorJson.has("message")) {

                            errorMsg =
                                    errorJson.getString(
                                            "message"
                                    );

                        } else if (errorJson.has("error")) {

                            errorMsg =
                                    errorJson.getString(
                                            "error"
                                    );
                        }

                    } catch (Exception ignored) {
                    }

                    final String displayError =
                            errorMsg;

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Upload failed"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                displayError,
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } else if (responseCode == 401) {

                    loginPreferences
                            .edit()
                            .remove("token")
                            .apply();

                    getSharedPreferences(
                            "user_data",
                            MODE_PRIVATE
                    )
                            .edit()
                            .remove("token")
                            .apply();

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Session expired"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                "Session expired. Please login again.",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent =
                                new Intent(
                                        activity_resume_analyzer.this,
                                        activity_login.class
                                );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    });

                } else if (responseCode == 403) {

                    String forbiddenMsg =
                            "Access denied.";

                    try {

                        JSONObject errorJson =
                                new JSONObject(
                                        responseText
                                );

                        if (errorJson.has("message")) {

                            forbiddenMsg =
                                    errorJson.getString(
                                            "message"
                                    );
                        }

                    } catch (Exception ignored) {
                    }

                    final String displayMsg =
                            forbiddenMsg;

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Access denied"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                displayMsg,
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } else if (responseCode == 413) {

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "File too large"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                "Resume file is too large.",
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } else if (responseCode == 415) {

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Unsupported format"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                "Unsupported resume format.",
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } else if (responseCode >= 500) {

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Server error"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                "Server error. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    });

                } else {

                    runOnUiThread(() -> {

                        setLoading(false);

                        tvUploadStatus.setText(
                                "Analysis failed"
                        );

                        Toast.makeText(
                                activity_resume_analyzer.this,
                                "Server error (" +
                                        responseCode +
                                        "). Please try again later.",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            } catch (Exception e) {

                final String errorMsg;

                if (
                        e instanceof
                                java.net.SocketTimeoutException
                ) {

                    errorMsg =
                            "Analysis timed out. The server is taking longer than usual.";

                } else if (
                        e instanceof
                                java.net.ConnectException
                ) {

                    errorMsg =
                            "Unable to connect to server";

                } else {

                    errorMsg =
                            "Unable to connect to server";
                }

                runOnUiThread(() -> {

                    setLoading(false);

                    tvUploadStatus.setText(
                            "Connection failed"
                    );

                    Toast.makeText(
                            activity_resume_analyzer.this,
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

    private void loadRecentAnalyses() {

        String token =
                getCleanToken();

        if (token.isEmpty()) {

            tvNoRecent.setVisibility(
                    View.VISIBLE
            );

            recentAnalyses.removeAllViews();

            return;
        }

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                URL url =
                        new URL(
                                ApiConfig.getResumeHistoryUrl()
                        );

                connection =
                        (HttpURLConnection)
                                url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(12000);

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + token
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                int code =
                        connection.getResponseCode();

                InputStream stream =
                        (code >= 200 && code < 300)
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                StringBuilder sb =
                        new StringBuilder();

                if (stream != null) {

                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            stream,
                                            "UTF-8"
                                    )
                            );

                    String line;

                    while (
                            (line =
                                    reader.readLine())
                                    != null
                    ) {

                        sb.append(line);
                    }

                    reader.close();
                }

                String responseBody =
                        sb.toString();

                if (
                        code >= 200
                                && code < 300
                ) {

                    JSONArray historyArray =
                            new JSONArray(
                                    responseBody
                            );

                    runOnUiThread(
                            () -> populateRecentAnalyses(
                                    historyArray
                            )
                    );

                } else {

                    runOnUiThread(() -> {

                        tvNoRecent.setVisibility(
                                View.VISIBLE
                        );

                        recentAnalyses
                                .removeAllViews();
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {

                    tvNoRecent.setVisibility(
                            View.VISIBLE
                    );

                    recentAnalyses
                            .removeAllViews();
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    private void populateRecentAnalyses(
            JSONArray historyArray
    ) {

        recentAnalyses.removeAllViews();

        if (
                historyArray == null
                        || historyArray.length() == 0
        ) {

            tvNoRecent.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        tvNoRecent.setVisibility(
                View.GONE
        );

        int count =
                Math.min(
                        historyArray.length(),
                        5
                );

        for (int i = 0; i < count; i++) {

            JSONObject item =
                    historyArray.optJSONObject(i);

            if (item == null) {
                continue;
            }

            int score =
                    item.optInt(
                            "score",
                            0
                    );

            String analyzedAt =
                    item.optString(
                            "analyzedAt",
                            ""
                    );

            String skills =
                    item.optString(
                            "skills",
                            ""
                    );

            String dateDisplay =
                    "Recently";

            if (analyzedAt.length() >= 10) {

                dateDisplay =
                        analyzedAt.substring(
                                0,
                                10
                        );
            }

            LinearLayout card =
                    new LinearLayout(this);

            card.setOrientation(
                    LinearLayout.VERTICAL
            );

            card.setBackgroundResource(
                    R.drawable.card_bg
            );

            int p16 =
                    (int)
                            (
                                    14 *
                                            getResources()
                                                    .getDisplayMetrics()
                                                    .density
                            );

            card.setPadding(
                    p16,
                    p16,
                    p16,
                    p16
            );

            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            lp.setMargins(
                    0,
                    0,
                    0,
                    (int)
                            (
                                    10 *
                                            getResources()
                                                    .getDisplayMetrics()
                                                    .density
                            )
            );

            card.setLayoutParams(lp);

            LinearLayout topRow =
                    new LinearLayout(this);

            topRow.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            topRow.setGravity(
                    Gravity.CENTER_VERTICAL
            );

            TextView scoreBadge =
                    new TextView(this);

            scoreBadge.setText(
                    score + "% ATS"
            );

            scoreBadge.setTextSize(14);

            scoreBadge.setTextColor(
                    getColor(R.color.text)
            );

            scoreBadge.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            scoreBadge.setBackgroundResource(
                    R.drawable.badge_bg
            );

            int pad =
                    (int)
                            (
                                    6 *
                                            getResources()
                                                    .getDisplayMetrics()
                                                    .density
                            );

            scoreBadge.setPadding(
                    pad * 2,
                    pad,
                    pad * 2,
                    pad
            );

            TextView dateText =
                    new TextView(this);

            LinearLayout.LayoutParams dateLp =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );

            dateText.setLayoutParams(
                    dateLp
            );

            dateText.setText(
                    dateDisplay
            );

            dateText.setGravity(
                    Gravity.END
            );

            dateText.setTextColor(
                    getColor(R.color.text_dark)
            );

            dateText.setTextSize(12);

            topRow.addView(
                    scoreBadge
            );

            topRow.addView(
                    dateText
            );

            card.addView(
                    topRow
            );

            if (!skills.isEmpty()) {

                TextView skillsText =
                        new TextView(this);

                skillsText.setText(
                        "Skills: " + skills
                );

                skillsText.setTextColor(
                        getColor(R.color.text_dark)
                );

                skillsText.setTextSize(13);

                skillsText.setMaxLines(2);

                skillsText.setEllipsize(
                        android.text.TextUtils.TruncateAt.END
                );

                LinearLayout.LayoutParams skillsLp =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                skillsLp.setMargins(
                        0,
                        (int)
                                (
                                        8 *
                                                getResources()
                                                        .getDisplayMetrics()
                                                        .density
                                ),
                        0,
                        0
                );

                skillsText.setLayoutParams(
                        skillsLp
                );

                card.addView(
                        skillsText
                );
            }

            recentAnalyses.addView(
                    card
            );
        }
    }

    private void setLoading(
            boolean loading
    ) {

        isAnalyzing = loading;

        btnAnalyze.setEnabled(
                !loading
        );

        btnChooseFile.setEnabled(
                !loading
        );

        progressAnalyze.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );

        if (loading) {

            btnAnalyze.setText(
                    "Analyzing..."
            );

            tvUploadStatus.setText(
                    "Analyzing resume with AI..."
            );

        } else {

            btnAnalyze.setText(
                    "Analyze Resume"
            );
        }
    }

    private String getFileMimeType(
            String fileName,
            Uri uri
    ) {

        String lower =
                fileName.toLowerCase(
                        Locale.US
                );

        if (lower.endsWith(".pdf")) {

            return "application/pdf";

        } else if (lower.endsWith(".docx")) {

            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        } else if (lower.endsWith(".doc")) {

            return "application/msword";
        }

        try {

            String type =
                    getContentResolver()
                            .getType(uri);

            if (
                    type != null
                            && !type.isEmpty()
            ) {

                return type;
            }

        } catch (Exception ignored) {
        }

        return "application/pdf";
    }

    private String getFileNameFromUri(
            Uri uri
    ) {

        String result = null;

        try (
                Cursor cursor =
                        getContentResolver()
                                .query(
                                        uri,
                                        null,
                                        null,
                                        null,
                                        null
                                )
        ) {

            if (
                    cursor != null
                            && cursor.moveToFirst()
            ) {

                int nameIndex =
                        cursor.getColumnIndex(
                                OpenableColumns.DISPLAY_NAME
                        );

                if (nameIndex >= 0) {

                    result =
                            cursor.getString(
                                    nameIndex
                            );
                }
            }

        } catch (Exception ignored) {
        }

        if (result == null) {

            String path =
                    uri.getPath();

            if (path != null) {

                int cut =
                        path.lastIndexOf('/');

                if (cut != -1) {

                    result =
                            path.substring(
                                    cut + 1
                            );
                }
            }
        }

        if (
                result == null
                        || result.isEmpty()
        ) {

            result =
                    "Resume.pdf";
        }

        return result;
    }

    private long getFileSizeFromUri(
            Uri uri
    ) {

        long size = 0;

        try (
                Cursor cursor =
                        getContentResolver()
                                .query(
                                        uri,
                                        null,
                                        null,
                                        null,
                                        null
                                )
        ) {

            if (
                    cursor != null
                            && cursor.moveToFirst()
            ) {

                int sizeIndex =
                        cursor.getColumnIndex(
                                OpenableColumns.SIZE
                        );

                if (sizeIndex >= 0) {

                    size =
                            cursor.getLong(
                                    sizeIndex
                            );
                }
            }

        } catch (Exception ignored) {
        }

        if (size <= 0) {

            try (
                    InputStream is =
                            getContentResolver()
                                    .openInputStream(uri)
            ) {

                if (is != null) {
                    size = is.available();
                }

            } catch (Exception ignored) {
            }
        }

        return size;
    }

    private String formatFileSize(
            long bytes
    ) {

        if (bytes <= 0) {
            return "0 KB";
        }

        if (bytes < 1024 * 1024) {

            return
                    (bytes / 1024)
                            + " KB";
        }

        return String.format(
                Locale.US,
                "%.1f MB",
                bytes /
                        (1024.0 * 1024.0)
        );
    }
}