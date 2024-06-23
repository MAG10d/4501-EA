package com.example.ea4501;

import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextInputEditText playerNameInput;
    private ImageButton playButton; // Changed from Button to ImageButton
    private Button submitButton, nextButton, continueButton;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
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
    }

    private void startGame() {
        playerNameInput.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
        timerView.setVisibility(View.VISIBLE);  // Ensure timer is visible
        generateQuestions();
        currentQuestionIndex = 0;
        correctAnswers = 0;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0); // Start the timer
        updateQuestionCounter();
        showQuestion();
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
    }

    private void checkAnswer() {
        String answerText = answerInput.getText().toString().trim();
        if (!answerText.isEmpty()) {
        try {
            int answer = Integer.parseInt(answerText);
            if (answer == answers[currentQuestionIndex]) {
                correctAnswers++;
                resultView.setText("Correct!");
            } else {
                resultView.setText("Incorrect! The correct answer was " + answers[currentQuestionIndex]);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
            }
            submitButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
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
        }
    }

    private void endGame() {
        long endTime = System.currentTimeMillis();
        int totalTime = (int) ((endTime - startTime) / 1000); // total time in seconds
        resultView.setText(String.format("%s, you got %d out of 10 correct! Total time: %d seconds", playerName, correctAnswers, totalTime));
        playerNameInput.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);
        questionView.setVisibility(View.GONE);
        answerInput.setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        timerView.setVisibility(View.GONE);
        questionCounterView.setVisibility(View.GONE);
        timerHandler.removeCallbacks(timerRunnable); // Stop the timer
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
}