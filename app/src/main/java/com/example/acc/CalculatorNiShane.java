package com.example.acc;

import static com.example.acc.R.*;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalculatorNiShane extends AppCompatActivity {

    private Button backToMainMenuButton;
    private Button backToUserActivityButton;
    private Spinner budgetSpinner;
    private TextView budgetText;
    private ProgressBar budgetProgressBar;
    private TextView loginStreakText;
    private TextView rewardPointsText;
    private TextView nextRewardText;
    private TextView todaysRewardText;
    private Button loginButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_STREAK = "loginStreak";
    private static final String KEY_POINTS = "points";
    private static final String KEY_BUDGET_CONFIRMED = "budgetConfirmed";
    private static final String KEY_BUDGET = "budget";
    private static final String KEY_APP_RESTARTED = "appRestarted";
    private static final String KEY_LAST_CHECK_IN = "lastCheckIn";

    private boolean hasCheckedIn = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator_ni_shane);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupSharedPreferences();
        setupBudgetSpinner();
        setupListeners();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("itemPrice")) {
            int itemPrice = intent.getIntExtra("itemPrice", 0);
            updateBudget(itemPrice);
        }
    }

    private void initializeViews() {
        backToMainMenuButton = findViewById(R.id.backToMainMenuButton);
        backToUserActivityButton = findViewById(R.id.backToUserActivityButton);
        budgetSpinner = findViewById(R.id.budgetSpinner);
        budgetText = findViewById(R.id.budgetText);
        budgetProgressBar = findViewById(R.id.budgetProgressBar);
        loginStreakText = findViewById(R.id.loginStreakText);
        rewardPointsText = findViewById(R.id.rewardPointsText);
        nextRewardText = findViewById(R.id.nextRewardText);
        todaysRewardText = findViewById(R.id.todaysRewardText);
        loginButton = findViewById(R.id.loginButton);
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean appRestarted = sharedPreferences.getBoolean(KEY_APP_RESTARTED, true);

        if (appRestarted) {
            sharedPreferences.edit()
                    .putInt(KEY_STREAK, 0)
                    .putInt(KEY_POINTS, 0)
                    .putInt(KEY_BUDGET, 0)
                    .putBoolean(KEY_BUDGET_CONFIRMED, false)
                    .putBoolean(KEY_APP_RESTARTED, false)
                    .apply();
        }

        int loginStreak = sharedPreferences.getInt(KEY_STREAK, 0);
        int points = sharedPreferences.getInt(KEY_POINTS, 0);
        boolean budgetConfirmed = sharedPreferences.getBoolean(KEY_BUDGET_CONFIRMED, false);
        int budget = sharedPreferences.getInt(KEY_BUDGET, 0);

        loginStreakText.setText("Current Streak: Day " + loginStreak);
        rewardPointsText.setText("Points: " + points);
        updateNextRewardText(points);
        budgetText.setText("Budget: " + budget + " pesos");
        budgetProgressBar.setProgress(budget);
        budgetSpinner.setVisibility((budget <= 0 || !budgetConfirmed) ? View.VISIBLE : View.GONE);
    }

    private void setupBudgetSpinner() {
        String[] budgetOptions = new String[]{"Input a budget", "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetSpinner.setAdapter(adapter);

        budgetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBudget = budgetOptions[position];
                if (!selectedBudget.equals("Input a budget")) {
                    confirmBudget(selectedBudget);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void confirmBudget(String selectedBudget) {
        new AlertDialog.Builder(CalculatorNiShane.this)
                .setTitle("Confirm Budget")
                .setMessage("Are you sure you want to set the budget to " + selectedBudget + " pesos?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    int budgetValue = Integer.parseInt(selectedBudget);
                    budgetText.setText("Budget: " + budgetValue + " pesos");
                    budgetProgressBar.setProgress(budgetValue);
                    budgetSpinner.setVisibility(View.GONE);
                    sharedPreferences.edit()
                            .putBoolean(KEY_BUDGET_CONFIRMED, true)
                            .putInt(KEY_BUDGET, budgetValue)
                            .apply();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());
        todaysRewardText.setOnClickListener(v -> showTodaysReward());
        nextRewardText.setOnClickListener(v -> showBadgeRequirements());
        backToMainMenuButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalculatorNiShane.this, Javalysus.class);
            intent.putExtra("budget", sharedPreferences.getInt(KEY_BUDGET, 0)); // Pass budget
            startActivity(intent);
        });
        backToUserActivityButton.setOnClickListener(v -> startActivity(new Intent(CalculatorNiShane.this, UserProfileActivity.class)));
    }

    private void handleLogin() {
        if (hasCheckedIn) {
            new AlertDialog.Builder(CalculatorNiShane.this)
                    .setTitle("Already Checked In")
                    .setMessage("You have already checked in for today. Please restart the app to check in again.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        hasCheckedIn = true;

        int loginStreak = sharedPreferences.getInt(KEY_STREAK, 0) + 1;
        int points = sharedPreferences.getInt(KEY_POINTS, 0) + getPointsForToday();

        loginStreakText.setText("Current Streak: Day " + loginStreak);
        rewardPointsText.setText("Points: " + points);

        sharedPreferences.edit()
                .putInt(KEY_STREAK, loginStreak)
                .putInt(KEY_POINTS, points)
                .putString(KEY_LAST_CHECK_IN, currentDate)
                .apply();

        updateNextRewardText(points);
        new AlertDialog.Builder(CalculatorNiShane.this)
                .setTitle("Check In Successful")
                .setMessage("You have successfully checked in for today!")
                .setPositiveButton("OK", null)
                .show();
    }

    private int getPointsForToday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY: return 100;
            case Calendar.TUESDAY: return 50;
            case Calendar.WEDNESDAY: return 100;
            case Calendar.THURSDAY: return 50;
            case Calendar.FRIDAY: return 100;
            case Calendar.SATURDAY: return 50;
            case Calendar.SUNDAY: return 150;
            default: return 0;
        }
    }

    private void updateNextRewardText(int points) {
        String nextBadge = "";
        int nextRewardPoints = 0;

        if (points < 100) {
            nextBadge = "First Badge";
            nextRewardPoints = 100 - points;
        } else if (points < 250) {
            nextBadge = "Bronze Badge";
            nextRewardPoints = 250 - points;
        } else if (points < 500) {
            nextBadge = "Silver Badge";
            nextRewardPoints = 500 - points;
        } else if (points < 1000) {
            nextBadge = "Gold Badge";
            nextRewardPoints = 1000 - points;
        } else if (points < 5000) {
            nextBadge = "Platinum Badge";
            nextRewardPoints = 5000 - points;
        } else {
            nextBadge = "Max Badge Achieved";
            nextRewardPoints = 0;
        }

        nextRewardText.setText("Next reward: " + nextRewardPoints + " points for " + nextBadge);
    }

    private void showTodaysReward() {
        String message = "You will receive " + getPointsForToday() + " points for today!\n\nPoints for each day:\n" +
                "Monday: +100 points\n" +
                "Tuesday: +50 points\n" +
                "Wednesday: +100 points\n" +
                "Thursday: +50 points\n" +
                "Friday: +100 points\n" +
                "Saturday: +50 points\n" +
                "Sunday: +150 points";
        new AlertDialog.Builder(CalculatorNiShane.this)
                .setTitle("Today's Reward")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showBadgeRequirements() {
        String message = "Badge Requirements:\n" +
                "First Badge: 100 points\n" +
                "Bronze Badge: 250 points\n" +
                "Silver Badge: 500 points\n" +
                "Gold Badge: 1000 points\n" +
                "Platinum Badge: 5000 points\n" +
                "Diamond Badge: 10000 points";

        new AlertDialog.Builder(CalculatorNiShane.this)
                .setTitle("Badge Requirements")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public void updateBudget(int amount) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentBudget = sharedPreferences.getInt(KEY_BUDGET, 0);
        int newBudget = currentBudget - amount;

        editor.putInt(KEY_BUDGET, newBudget);
        editor.apply();

        budgetProgressBar.setProgress(newBudget);
        budgetText.setText("Budget: " + newBudget + " pesos");
    }
}