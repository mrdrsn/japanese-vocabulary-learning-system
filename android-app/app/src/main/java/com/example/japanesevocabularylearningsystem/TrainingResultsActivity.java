package com.example.japanesevocabularylearningsystem;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_results);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}