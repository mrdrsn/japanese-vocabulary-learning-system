package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingFinishedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_finished);

        int correctCount = getIntent().getIntExtra("EXTRA_CORRECT_COUNT", 0);
        int totalCount   = getIntent().getIntExtra("EXTRA_TOTAL_COUNT", 0);

        ((TextView) findViewById(R.id.tvScore))
                .setText(correctCount + " / " + totalCount);

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