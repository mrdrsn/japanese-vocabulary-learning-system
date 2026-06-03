package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ModeChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choose);

        String scenarioId   = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_ID);
        String scenarioName = getIntent().getStringExtra(MainActivity.EXTRA_SCENARIO_NAME);
        if (scenarioId == null) scenarioId = "SC1";
        if (scenarioName == null) scenarioName = "Convenience Store";

        final String sid  = scenarioId;
        final String sname = scenarioName;

        ImageView btnMemorizationMode = findViewById(R.id.btnMemorizationMode);
        ImageView btnTrainingMode     = findViewById(R.id.btnTrainingMode);
        ImageView btnBack             = findViewById(R.id.btnBack);

        btnMemorizationMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, LexicalViewActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, sid);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, sname);
            startActivity(intent);
            finish();
        });

        btnTrainingMode.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrainingMenuActivity.class);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_ID, sid);
            intent.putExtra(MainActivity.EXTRA_SCENARIO_NAME, sname);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }
}