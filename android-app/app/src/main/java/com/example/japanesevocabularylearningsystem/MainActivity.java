package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_SCENARIO_ID   = "scenarioId";
    public static final String EXTRA_SCENARIO_NAME = "scenarioName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView btnFoodScenario  = findViewById(R.id.btnFoodScenario);
        ImageView btnStoreScenario = findViewById(R.id.btnStoreScenario);

        btnStoreScenario.setOnClickListener(v ->
                openModeChoose("SC1", "Convenience Store"));

        btnFoodScenario.setOnClickListener(v ->
                openModeChoose("SC2", "Cafe / Restaurant"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void openModeChoose(String scenarioId, String scenarioName) {
        Intent intent = new Intent(this, ModeChooseActivity.class);
        intent.putExtra(EXTRA_SCENARIO_ID, scenarioId);
        intent.putExtra(EXTRA_SCENARIO_NAME, scenarioName);
        startActivity(intent);
        finish();
    }
}