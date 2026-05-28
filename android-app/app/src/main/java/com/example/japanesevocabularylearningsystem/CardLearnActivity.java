package com.example.japanesevocabularylearningsystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.japanesevocabularylearningsystem.data.MockDataProvider;
import com.example.japanesevocabularylearningsystem.model.CommunicativeIntent;
import com.example.japanesevocabularylearningsystem.model.Role;
import com.example.japanesevocabularylearningsystem.model.Utterance;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class CardLearnActivity extends AppCompatActivity {

    private static ArrayList<String> savedQueue = null;
    private static int savedLearnedCount = 0;
    private static ArrayList<String> savedStepIdsCache = null;

    private ImageView btnBack, btnCheck, btnCross;
    private TextView tvScenarioName, tvCardQuantity;
    private ProgressBar progressBar;
    private CardView frontCard, backCard;
    private TextView tvRomaji, tvTranslation, tvRoleValue, tvIntentValue;
    private FrameLayout cardContainer;

    private ArrayDeque<String> cardQueue;
    private int learnedCount;
    private int totalCards;
    private boolean isShowingFront = true;
    private boolean isAnimating = false;

    private List<Utterance> activeUtterances;
    private ArrayList<String> receivedStepIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_learn);

        btnBack       = findViewById(R.id.btnBack);
        btnCheck      = findViewById(R.id.btnCheck);
        btnCross      = findViewById(R.id.btnCross);
        tvScenarioName = findViewById(R.id.tvScenarioName);
        tvCardQuantity = findViewById(R.id.tvCardQuantity);
        progressBar   = findViewById(R.id.progressBar);
        frontCard     = findViewById(R.id.frontCard);
        backCard      = findViewById(R.id.backCard);
        tvRomaji      = findViewById(R.id.tvRomaji);
        tvTranslation = findViewById(R.id.tvTranslation);
        tvRoleValue   = findViewById(R.id.tvRoleValue);
        tvIntentValue = findViewById(R.id.tvIntentValue);
        cardContainer = findViewById(R.id.cardContainer);

        receivedStepIds = getIntent().getStringArrayListExtra("stepIds");
        ArrayList<String> utteranceIds = getIntent().getStringArrayListExtra("utteranceIds");

        if (utteranceIds != null && !utteranceIds.isEmpty()) {
            activeUtterances = new ArrayList<>();
            for (String id : utteranceIds) {
                Utterance u = MockDataProvider.getUtteranceById(id);
                if (u != null) activeUtterances.add(u);
            }
        } else {
            activeUtterances = new ArrayList<>(MockDataProvider.getConvenienceStoreUtterances());
        }

        totalCards = activeUtterances.size();
        progressBar.setMax(totalCards);

        if (savedQueue != null && listsEqual(receivedStepIds, savedStepIdsCache)) {
            cardQueue    = new ArrayDeque<>(savedQueue);
            learnedCount = savedLearnedCount;
        } else {
            cardQueue = new ArrayDeque<>();
            for (Utterance u : activeUtterances) cardQueue.add(u.getId());
            learnedCount = 0;
        }

        float density = getResources().getDisplayMetrics().density;
        frontCard.setCameraDistance(8000 * density);
        backCard.setCameraDistance(8000 * density);

        updateProgress();
        showCurrentCard();

        // нажатие на карточку: лицо → изнанка, изнанка → лицо
        cardContainer.setOnClickListener(v -> {
            if (isAnimating) return;
            if (isShowingFront) flipToBack();
            else flipToFront();
        });

        // ✓ — знаю: карточка улетает вправо, следующая приходит слева
        btnCheck.setOnClickListener(v -> {
            if (isAnimating || cardQueue.isEmpty()) return;
            isAnimating = true;
            swipeCardOut(true, () -> {
                cardQueue.poll();
                learnedCount++;
                if (cardQueue.isEmpty()) {
                    clearSavedState();
                    startActivity(new Intent(this, CardLearnFinishedActivity.class));
                    finish();
                } else {
                    updateProgress();
                    showCurrentCard();
                    slideCardIn(false); // входит слева
                }
            });
        });

        // ✗ — не знаю: карточка улетает влево, следующая приходит справа
        btnCross.setOnClickListener(v -> {
            if (isAnimating || cardQueue.isEmpty()) return;
            isAnimating = true;
            swipeCardOut(false, () -> {
                cardQueue.add(cardQueue.poll()); // отправить в конец
                showCurrentCard();
                slideCardIn(true); // входит справа
            });
        });

        btnBack.setOnClickListener(v -> goBack());
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        saveState();
        finish();
    }

    // ── Переворот карточки ───────────────────────────────────────────────────

    private void flipToBack() {
        isAnimating = true;
        float density = getResources().getDisplayMetrics().density;
        frontCard.setCameraDistance(8000 * density);
        backCard.setCameraDistance(8000 * density);

        ObjectAnimator out = ObjectAnimator.ofFloat(frontCard, "rotationY", 0f, 90f);
        out.setDuration(150);
        out.setInterpolator(new AccelerateInterpolator());
        out.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontCard.setVisibility(View.GONE);
                backCard.setVisibility(View.VISIBLE);
                backCard.setRotationY(-90f);
                ObjectAnimator in = ObjectAnimator.ofFloat(backCard, "rotationY", -90f, 0f);
                in.setDuration(150);
                in.setInterpolator(new DecelerateInterpolator());
                in.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                    }
                });
                in.start();
            }
        });
        out.start();
        isShowingFront = false;
    }

    private void flipToFront() {
        isAnimating = true;
        float density = getResources().getDisplayMetrics().density;
        frontCard.setCameraDistance(8000 * density);
        backCard.setCameraDistance(8000 * density);

        ObjectAnimator out = ObjectAnimator.ofFloat(backCard, "rotationY", 0f, 90f);
        out.setDuration(150);
        out.setInterpolator(new AccelerateInterpolator());
        out.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backCard.setVisibility(View.GONE);
                frontCard.setVisibility(View.VISIBLE);
                frontCard.setRotationY(-90f);
                ObjectAnimator in = ObjectAnimator.ofFloat(frontCard, "rotationY", -90f, 0f);
                in.setDuration(150);
                in.setInterpolator(new DecelerateInterpolator());
                in.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimating = false;
                    }
                });
                in.start();
            }
        });
        out.start();
        isShowingFront = true;
    }

    // ── Swipe-анимации (Quizlet-стиль) ───────────────────────────────────────

    private void swipeCardOut(boolean toRight, Runnable onComplete) {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float target = toRight ? screenWidth * 1.5f : -screenWidth * 1.5f;

        ObjectAnimator swipe = ObjectAnimator.ofFloat(cardContainer, "translationX", 0f, target);
        swipe.setDuration(220);
        swipe.setInterpolator(new AccelerateInterpolator());
        swipe.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardContainer.setTranslationX(0f);
                onComplete.run();
            }
        });
        swipe.start();
    }

    private void slideCardIn(boolean fromRight) {
        float screenWidth = getResources().getDisplayMetrics().widthPixels;
        float startX = fromRight ? screenWidth * 1.5f : -screenWidth * 1.5f;

        cardContainer.setTranslationX(startX);
        ObjectAnimator slide = ObjectAnimator.ofFloat(cardContainer, "translationX", startX, 0f);
        slide.setDuration(220);
        slide.setInterpolator(new DecelerateInterpolator());
        slide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }
        });
        slide.start();
    }

    // ── Отображение карточки ─────────────────────────────────────────────────

    private void showCurrentCard() {
        if (cardQueue.isEmpty()) return;
        Utterance utterance = MockDataProvider.getUtteranceById(cardQueue.peek());
        if (utterance == null) return;

        tvRomaji.setText(utterance.getSurfaceRomaji());
        tvTranslation.setText(utterance.getTranslation());

        Role role = MockDataProvider.getRoleById(utterance.getRoleId());
        CommunicativeIntent intent = MockDataProvider.getIntentById(utterance.getCommunicativeIntentId());

        tvRoleValue.setText(role != null ? role.getName() : "—");
        tvIntentValue.setText(intent != null ? intent.getName() : "—");

        // всегда начинаем с лицевой стороны
        isShowingFront = true;
        frontCard.setVisibility(View.VISIBLE);
        frontCard.setRotationY(0f);
        backCard.setVisibility(View.GONE);
        backCard.setRotationY(0f);
    }

    private void updateProgress() {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(
                progressBar, "progress",
                progressBar.getProgress(), learnedCount);
        progressAnimator.setDuration(400);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.start();

        tvCardQuantity.setText(learnedCount + "/" + totalCards + " карточек изучено");
    }

    // ── Сохранение / восстановление прогресса ────────────────────────────────

    private void saveState() {
        savedQueue        = new ArrayList<>(cardQueue);
        savedLearnedCount = learnedCount;
        savedStepIdsCache = receivedStepIds;
    }

    public static void clearSavedState() {
        savedQueue        = null;
        savedLearnedCount = 0;
        savedStepIdsCache = null;
    }

    private static boolean listsEqual(ArrayList<String> a, ArrayList<String> b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.size() == b.size() && a.containsAll(b);
    }
}