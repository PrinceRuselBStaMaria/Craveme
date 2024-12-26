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
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import android.util.Log;

public class UserProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText, passwordText, usernameText;
    private Button logoutButton;
    private ImageView profileImage;

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

        logoutButton = findViewById(R.id.logoutButton);
        profileImage = findViewById(R.id.profileImage);

        // Load user data
        displayUserInfo();

        // Set up logout button
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void displayUserInfo() {
        SharedPreferences prefs = getSharedPreferences("UserCredentials", Context.MODE_PRIVATE);
        
        String name = prefs.getString("firstName", "Not set");
        String email = prefs.getString("email", "Not set");
        String imagePath = prefs.getString("profileImage", null);

        String username = prefs.getString("username", "Not set");
        String password = prefs.getString("password", "Not set");
        
        Log.d("UserProfile", "Retrieved image path: " + imagePath);
        
        nameText.setText("Name: " + name);
        emailText.setText("Email: " + email);
        passwordText.setText("Password: " + password);
        usernameText.setText("Username: " + username);

        
        if (imagePath != null && !imagePath.isEmpty()) {
            File imageFile = new File(imagePath);
            Log.d("UserProfile", "Image file exists: " + imageFile.exists());
            
            if (imageFile.exists()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null) {
                        profileImage.setImageBitmap(bitmap);
                        profileImage.setVisibility(View.VISIBLE);
                        Log.d("UserProfile", "Image loaded successfully");
                    } else {
                        Log.e("UserProfile", "Failed to decode bitmap");
                        profileImage.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("UserProfile", "Error loading image: " + e.getMessage());
                    e.printStackTrace();
                    profileImage.setVisibility(View.GONE);
                }
            } else {
                Log.e("UserProfile", "Image file does not exist");
                profileImage.setVisibility(View.GONE);
            }
        } else {
            Log.d("UserProfile", "No image path found");
            profileImage.setVisibility(View.GONE);
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