package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModeChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mode_choose);
        ImageView btnMemorizationMode = findViewById(R.id.btnMemorizationMode);
        ImageView btnTrainingMode = findViewById(R.id.btnTrainingMode);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnMemorizationMode.setOnClickListener(v -> {
            Intent intent = new Intent(ModeChooseActivity.this, LexicalViewActivity.class);
            startActivity(intent);
            finish();
        });

        btnTrainingMode.setOnClickListener(v -> {
            Intent intent = new Intent(ModeChooseActivity.this, TrainingActivity.class);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ModeChooseActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}