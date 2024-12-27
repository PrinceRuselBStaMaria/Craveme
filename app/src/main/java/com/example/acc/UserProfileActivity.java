package com.example.acc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import android.util.Log;

public class UserProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText, passwordText, usernameText, birthdayText;
    private Button logoutButton;
    private ImageView profileImage;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);

        passwordText = findViewById(R.id.passwordText);
        usernameText = findViewById(R.id.usernameText);
        birthdayText = findViewById(R.id.birthdayText);
        backButton = findViewById(R.id.back_button);

        logoutButton = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);

        // Load user data
        displayUserInfo();

        // Set up logout button
        logoutButton.setOnClickListener(v -> handleLogout());
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, Javalysus.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        
        String name = prefs.getString("firstName", "Not set");
        String email = prefs.getString("email", "Not set");
        String imagePath = prefs.getString("profileImage", null);
        String username = prefs.getString("username", "Not set");
        String password = prefs.getString("password", "Not set");
        String birthday = prefs.getString("birthday", "Not set");
        
        Log.d("UserProfile", "Loading image from path: " + imagePath);
        
        nameText.setText("Name: " + name);
        emailText.setText("Email: " + email);
        passwordText.setText("Password: " + password);
        usernameText.setText("Username: " + username);
        birthdayText.setText("Birthday: " + birthday);

        if (imagePath != null) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null) {
                        profileImage.setImageBitmap(bitmap);
                        Log.d("UserProfile", "Loaded image from: " + imagePath);
                    }
                } catch (Exception e) {
                    Log.e("UserProfile", "Error loading image", e);
                    profileImage.setImageResource(R.drawable.devtry);
                }
            }
        }
    }

    private void handleLogout() {
        // Clear user data
        User.clearLocalStorage(this);
        
        // Navigate back to login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}