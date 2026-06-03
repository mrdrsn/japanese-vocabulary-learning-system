package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingFinishedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_finished);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TrainingFinishedActivity.this, TrainingMenuActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID));
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME,
                    getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME));
            startActivity(intent);
            finish();
        });
    }
}