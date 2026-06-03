package com.example.japanesevocabularylearningsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TrainingMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            startActivity(new Intent(this, ModeChooseActivity.class));
            finish();
        });

        findViewById(R.id.btnStartTraining).setOnClickListener(v ->
                startActivity(new Intent(this, TrainingActivity.class)));
        // НЕ вызываем finish() — TrainingMenuActivity остаётся в back stack,
        // чтобы btnBack во фрагментах (finish()) возвращал сюда

        findViewById(R.id.btnViewResults).setOnClickListener(v ->
                startActivity(new Intent(this, TrainingResultsActivity.class)));
    }
}