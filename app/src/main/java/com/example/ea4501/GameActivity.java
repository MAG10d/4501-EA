package com.example.ea4501;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private TextView questionView, resultView;
    private EditText answerInput;
    private Button doneButton, nextButton, continueButton;
    private int currentQuestion = 0;
    private int correctCount = 0;
    private long startTime;
    private Handler timerHandler = new Handler();
    private int timeTaken;

    private int operand1, operand2, correctAnswer;
    private String operator;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionView = findViewById(R.id.questionView);
        resultView = findViewById(R.id.resultView);
        answerInput = findViewById(R.id.answerInput);
        doneButton = findViewById(R.id.doneButton);
        nextButton = findViewById(R.id.nextButton);
        continueButton = findViewById(R.id.continueButton);

        generateQuestion();
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void generateQuestion() {
        operand1 = random.nextInt(100) + 1;
        operand2 = random.nextInt(100) + 1;
        operator = getOperator();

        if (operator.equals("/")) {
            while (operand1 % operand2 != 0) {
                operand1 = random.nextInt(100) + 1;
                operand2 = random.nextInt(100) + 1;
            }
        } else if (operator.equals("-")) {
            while (operand1 < operand2) {
                operand1 = random.nextInt(100) + 1;
                operand2 = random.nextInt(100) + 1;
            }
        }

        switch (operator) {
            case "+":
                correctAnswer = operand1 + operand2;
                break;
            case "-":
                correctAnswer = operand1 - operand2;
                break;
            case "*":
                correctAnswer = operand1 * operand2;
                break;
            case "/":
                correctAnswer = operand1 / operand2;
                break;
        }

        questionView.setText(String.format("%d %s %d =", operand1, operator, operand2));
        answerInput.setText("");
        resultView.setText("");
    }

    private String getOperator() {
        String[] operators = {"+", "-", "*", "/"};
        return operators[random.nextInt(4)];
    }

    public void checkAnswer(View view) {
        String answerText = answerInput.getText().toString();
        if (!answerText.isEmpty()) {
            int answer = Integer.parseInt(answerText);
            if (answer == correctAnswer) {
                correctCount++;
                resultView.setText("Correct!");
            } else {
                resultView.setText("Incorrect!");
            }
            currentQuestion++;
            if (currentQuestion < 10) {
                nextButton.setVisibility(View.VISIBLE);
                doneButton.setVisibility(View.GONE);
            } else {
                showFinalResult();
            }
        }
    }

    public void nextQuestion(View view) {
        generateQuestion();
        nextButton.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
    }

    private void showFinalResult() {
        timerHandler.removeCallbacks(timerRunnable);
        timeTaken = (int) ((System.currentTimeMillis() - startTime) / 1000);
        resultView.setText(String.format("Game Over! You got %d/10 correct in %d seconds.", correctCount, timeTaken));
        continueButton.setVisibility(View.VISIBLE);
    }

    public void startNewGame(View view) {
        currentQuestion = 0;
        correctCount = 0;
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        generateQuestion();
        continueButton.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerHandler.postDelayed(this, 500);
        }
    };
}