package com.example.ea4501;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment {
    private TextInputLayout playerNameInputLayout;
    private TextInputEditText playerNameInput;
    private ImageButton playButton; // Changed from Button to ImageButton
    private Button submitButton, nextButton, continueButton, backButton;
    private TextView questionView, resultView, timerView, questionCounterView;
    private EditText answerInput;
    private String playerName;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private long startTime;
    private Handler timerHandler = new Handler();
    private Random random = new Random();
    private String[] questions = new String[10];  // Add this line to declare the questions array
    private int[] answers = new int[10];
    private MediaPlayer mediaPlayer;
    private MediaPlayer bgmPlayer; // MediaPlayer for background music
    private ImageView fireworkView; // Firework ImageView

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        playerNameInputLayout = view.findViewById(R.id.playerNameInputLayout);
        playerNameInput = view.findViewById(R.id.playerNameInput);
        playButton = view.findViewById(R.id.button_play);
        questionView = view.findViewById(R.id.questionView);
        resultView = view.findViewById(R.id.resultView);
        timerView = view.findViewById(R.id.timerView);
        questionCounterView = view.findViewById(R.id.questionCounterView);
        answerInput = view.findViewById(R.id.answerInput);
        submitButton = view.findViewById(R.id.submitButton);
        nextButton = view.findViewById(R.id.nextButton);
        continueButton = view.findViewById(R.id.continueButton);
        backButton = view.findViewById(R.id.backButton);
        fireworkView = view.findViewById(R.id.fireworkView);
    }

    private void setupListeners() {
        playButton.setOnClickListener(v -> {
                playerName = playerNameInput.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    startGame();
                } else {
                    Toast.makeText(getActivity(), "Please enter your name", Toast.LENGTH_SHORT).show();
                }
        });

        submitButton.setOnClickListener(v -> checkAnswer());
        nextButton.setOnClickListener(v -> nextQuestion());
        continueButton.setOnClickListener(v -> startGame());
        backButton.setOnClickListener(v -> backViews());
    }

    private void startGame() {
        playerNameInputLayout.setVisibility(View.GONE);
        playerNameInput.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        timerView.setVisibility(View.VISIBLE);  // Ensure timer is visible
        generateQuestions();
        currentQuestionIndex = 0;
        correctAnswers = 0;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0); // Start the timer
        updateQuestionCounter();
        showQuestion();
        startBackgroundMusic(); // Start background music
    }

    private void generateQuestions() {
        for (int i = 0; i < 10; i++) {
            int a = random.nextInt(100) + 1;
            int b = random.nextInt(100) + 1;
            char operator = '+';
            int answer = 0;

            switch (random.nextInt(4)) {
                case 0:
                    operator = '+';
                    answer = a + b;
                    break;
                case 1:
                    operator = '-';
                    if (a < b) {
                        int temp = a;
                        a = b;
                        b = temp;
                    }
                    answer = a - b;
                    break;
                case 2:
                    operator = '*';
                    answer = a * b;
                    break;
                case 3:
                    operator = '/';
                    while (b == 0 || a % b != 0) {
                        b = random.nextInt(100) + 1;
                    }
                    answer = a / b;
                    break;
            }
            questions[i] = String.format("%d %c %d = ?", a, operator, b);
            answers[i] = answer;
        }
    }

    private void updateQuestionCounter() {
        questionCounterView.setText(String.format("Question %d of 10", currentQuestionIndex + 1));
    }

    private void showQuestion() {
        questionView.setVisibility(View.VISIBLE);
        answerInput.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);
        updateQuestionCounter();

        questionView.setText(questions[currentQuestionIndex]);
        answerInput.setText("");
        resultView.setText("");
        resultView.setVisibility(View.GONE); // Hide resultView initially
    }

    private void checkAnswer() {
        String answerText = answerInput.getText().toString().trim();
        if (!answerText.isEmpty()) {
        try {
            int answer = Integer.parseInt(answerText);
            if (answer == answers[currentQuestionIndex]) {
                correctAnswers++;
                resultView.setText("Correct!");
                    playSound(R.raw.correct);
                    showFirework(); // Show firework animation
            } else {
                resultView.setText("Incorrect! The correct answer was " + answers[currentQuestionIndex]);
                    playSound(R.raw.incorrect);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
            }
            resultView.setVisibility(View.VISIBLE); // Make resultView visible
            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            applyAnimation(resultView); // Apply animation to resultView
        } else {
            Toast.makeText(getActivity(), "Please enter an answer", Toast.LENGTH_SHORT).show();
        }
    }

    private void nextQuestion() {
    if (currentQuestionIndex < 9) { // 9 because currentQuestionIndex is 0-based
        currentQuestionIndex++;
            showQuestion();
        } else {
            endGame();
            continueButton.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        }
    }

    private void endGame() {
        long endTime = System.currentTimeMillis();
        int totalTime = (int) ((endTime - startTime) / 1000); // total time in seconds
        int wrongAnswers = 10 - correctAnswers;
        resultView.setText(String.format("%s, you got %d out of 10 correct and %d wrong! Total time: %d seconds", playerName, correctAnswers, wrongAnswers, totalTime));
        resultView.setVisibility(View.VISIBLE); // Make resultView visible
        questionView.setVisibility(View.GONE);
        answerInput.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        timerView.setVisibility(View.GONE);
        questionCounterView.setVisibility(View.GONE);
        timerHandler.removeCallbacks(timerRunnable); // Stop the timer
        stopBackgroundMusic(); // Stop background music
        saveRecord(totalTime);
    }

    private void saveRecord(int totalTime) {
        SQLiteDatabase db = new GameDatabaseHelper(getActivity()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("playerName", playerName);
        values.put("playDate", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("playTime", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put("duration", totalTime);
        values.put("correctCount", correctAnswers);
        db.insert("GamesLog", null, values);
        db.close();
    Toast.makeText(getActivity(), "Record saved", Toast.LENGTH_SHORT).show();
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    private void playSound(int soundResId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(getActivity(), soundResId);
        mediaPlayer.start();
    }

    private void startBackgroundMusic() {
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(getActivity(), R.raw.background_music);
            bgmPlayer.setLooping(true); // Loop the background music
        }
        bgmPlayer.start();
}

    private void stopBackgroundMusic() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.stop();
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }

    private void applyAnimation(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        Animation scaleUp = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_up);
        view.startAnimation(fadeIn);
        view.startAnimation(scaleUp);
    }

    private void showFirework() {
        fireworkView.setVisibility(View.VISIBLE);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fireworkView, "scaleX", 0.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fireworkView, "scaleY", 0.1f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(fireworkView, "alpha", 1f, 0f);
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(1000);
        animatorSet.start();

        animatorSet.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) { }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                fireworkView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) { }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) { }
        });
    }

    private void backViews() {
        resultView.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        playerNameInputLayout.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
    }
}