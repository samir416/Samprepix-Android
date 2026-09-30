package com.example.samprepix;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class activity_resume_analyzer extends AppCompatActivity {

    Button btnChooseFile;

    LinearLayout recentAnalyses;

    TextView tvNoRecent;

    SharedPreferences preferences;

    ArrayList<String> fileNames = new ArrayList<>();
    ArrayList<String> fileUris = new ArrayList<>();

    ActivityResultLauncher<Intent> filePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resume_analyzer);

        btnChooseFile = findViewById(R.id.btn_choose_file);
        recentAnalyses = findViewById(R.id.recent_analyses);
        tvNoRecent = findViewById(R.id.tv_no_recent);

        preferences = getSharedPreferences(
                "resume_data",
                MODE_PRIVATE
        );

        filePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK) {

                        Intent data = result.getData();

                        if (data != null) {

                            Uri uri = data.getData();

                            if (uri != null) {
                                handleSelectedFile(uri);
                            }
                        }
                    }
                }
        );

        btnChooseFile.setOnClickListener(v -> {
            chooseFile();
        });

        loadRecentFiles();
    }

    private void chooseFile() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.setType("*/*");

        intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                new String[]{
                        "application/pdf",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                }
        );
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePicker.launch(intent);
    }

    private void handleSelectedFile(Uri uri){
        String filename = getFileName(uri);
        if(filename==null){
            Toast.makeText(this, "Unable to read file", Toast.LENGTH_SHORT).show();
            return;
        }

        String lowerName = filename.toLowerCase();
        if(!lowerName.endsWith(".pdf")&&!lowerName.endsWith(".docx")){
            Toast.makeText(this, "Only PDF and DOCX files are allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        long fileSize = getFileSize(uri);
        if(fileSize>5*1024*1024)
        {
            Toast.makeText(this, "File size must be less than 5MB", Toast.LENGTH_SHORT).show();
            return;
        }
        saveRecentFile(filename, uri.toString());
        Toast.makeText(this, "Resume selected successfully", Toast.LENGTH_SHORT).show();
        loadRecentFiles();

    }
    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }
    private long getFileSize(Uri uri){
        long fileSize=0;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if (cursor!=null){
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

            if (cursor.moveToFirst()){
                fileSize =cursor.getLong(sizeIndex);
            }
            cursor.close();
        }
        return fileSize;

    }

    private void saveRecentFile(String fileName , String uri)
    {

        fileNames.clear();
        fileUris.clear();

        Set<String>savedNames = preferences.getStringSet("file_names",new HashSet<>());
        Set<String>savedUris = preferences.getStringSet("file_uris",new HashSet<>());

        fileNames.addAll(savedNames);
        fileUris.addAll(savedUris);

        if (!fileNames.contains(fileName)) {
            fileNames.add(fileName);
            fileUris.add(uri);
        }

        SharedPreferences.Editor editor = preferences.edit();

        editor.putStringSet("file_names",new HashSet<>(fileNames));
        editor.putStringSet("file_uris",new HashSet<>(fileUris));
        editor.apply();
    }
    private void loadRecentFiles() {

        recentAnalyses.removeAllViews();
        Set<String> savedNames = preferences.getStringSet("file_names", new HashSet<>());
        Set<String> savedUris = preferences.getStringSet("file_uris", new HashSet<>());
        fileNames.clear();
        fileUris.clear();
        fileNames.addAll(savedNames);
        fileUris.addAll(savedUris);

        if (fileNames.isEmpty()) {
            tvNoRecent.setVisibility(View.VISIBLE);
            return;
        }
        tvNoRecent.setVisibility(View.GONE);
        for (int i = 0; i < fileNames.size(); i++) {

            String fileName = fileNames.get(i);
            String fileUri = fileUris.get(i);

            addRecentFile(fileName, fileUri);
        }
    }
    private void addRecentFile(String fileName , String fileUri){
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);
        card.setPadding(14,10,10,10);

        card.setBackgroundResource( R.drawable.card_bg);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,72);
        cardParams.setMargins(0,0,0,10);
        card.setLayoutParams(cardParams);
        TextView icon = new TextView(this);
        icon.setText("☰");
        icon.setTextColor(getColor(R.color.primary));
        icon.setTextSize(25);
        icon.setGravity(android.view.Gravity.CENTER);
        LinearLayout.LayoutParams iconParams =  new LinearLayout.LayoutParams(42,50);
        card.addView(icon,iconParams);
        LinearLayout information =  new LinearLayout(this);
        information.setOrientation( LinearLayout.VERTICAL);
        information.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams infoParams =  new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1);
        TextView name = new TextView(this);
        name.setText(fileName);
        name.setTextColor(getColor(R.color.text));
        name.setTextSize(13);
        name.setSingleLine(true);
        TextView details = new TextView(this);
        details.setText("Resume file");
        details.setTextColor(getColor(R.color.text_dark));
        details.setTextSize(11);
        information.addView(details);
        information.addView(name);
        card.addView(information, infoParams);

        Button viewbtn = new Button(this);
        viewbtn.setText("View");
        viewbtn.setAllCaps(false);
        viewbtn.setTextColor(getColor(R.color.green));
        viewbtn.setTextSize(12);
        viewbtn.setBackgroundResource(R.drawable.btn_outline);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(72,42);

        card.addView(viewbtn,buttonParams);

        viewbtn.setOnClickListener(v -> {

            Uri uri = Uri.parse(fileUri);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try{
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
            }
        });
        recentAnalyses.addView(card);

    }
}

