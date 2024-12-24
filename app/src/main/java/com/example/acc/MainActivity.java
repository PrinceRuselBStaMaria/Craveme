package com.example.acc;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView forgotPassword, registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        setupClickListeners();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        try {
            emailInput = findViewById(R.id.emailInput);
            passwordInput = findViewById(R.id.passwordInput);
            loginButton = findViewById(R.id.loginButton);
            forgotPassword = findViewById(R.id.forgotPassword);
            registerLink = findViewById(R.id.registerLink);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> handleLogin());
        }
        if (forgotPassword != null) {
            forgotPassword.setOnClickListener(v -> handleForgotPassword());
        }
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> handleRegister());
        }
    }

    private void handleLogin() {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", null);
        String savedPassword = sharedPreferences.getString("password", null);
        String firstName = sharedPreferences.getString("firstName", "");

        if (email.equals(savedUsername) && password.equals(savedPassword)) {
            Intent intent = new Intent(MainActivity.this, MainActivity3.class);
            intent.putExtra("fullName", firstName);
            startActivity(intent);
            finish();
        } else {
            new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Invalid credentials")
                .setPositiveButton("OK", null)
                .show();
        }
    }

    private void handleRegister() {
        startActivity(new Intent(MainActivity.this, MainActivity2.class));
    }

    private void handleForgotPassword() {
        Toast.makeText(this, "Forgot password feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void handleSocialLogin(String provider) {
        Toast.makeText(this, provider + " login coming soon", Toast.LENGTH_SHORT).show();
    }
}
