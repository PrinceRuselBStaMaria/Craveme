package com.example.acc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView named;
    private Button buttonGoToJavalysus;
    private TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        welcome =findViewById(R.id.welcomeText);
        named = findViewById(R.id.named);

        // Load animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        // Get the intent data
        String fullName = getIntent().getStringExtra("fullName");

        // Add null check and set text
        if (fullName != null && !fullName.isEmpty()) {
            named.setText(fullName + "!");
        } else {
            named.setText("No name provided");
        }

        // Start fade-in animation for the TextView and Button
        named.setVisibility(View.VISIBLE);
        welcome.setVisibility(View.VISIBLE);
        named.startAnimation(fadeIn);
        welcome.startAnimation(fadeIn);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start Javalysus Activity after fade-out animation
                named.setVisibility(View.INVISIBLE);
                welcome.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity3.this, Javalysus.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start fade-out animation after a 2-second delay
        new Handler().postDelayed(() -> {
            named.startAnimation(fadeOut);
            welcome.startAnimation(fadeOut);
        }, 2000); // 2000ms = 2 seconds delay
    }
}
