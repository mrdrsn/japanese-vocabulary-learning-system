package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);

        String scenarioId   = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        String scenarioName = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            Intent intent = new Intent(this, ModeChooseActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, scenarioName);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.btnStartTraining).setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, scenarioName);
            startActivity(intent);
            // НЕ вызываем finish() — остаётся в back stack
        });

        findViewById(R.id.btnViewResults).setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingResultsActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, scenarioId);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, scenarioName);
            startActivity(intent);
        });
    }
}