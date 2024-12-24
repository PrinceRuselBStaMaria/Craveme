package com.example.acc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acc.MainActivity;
import com.example.acc.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override   
            public void onAnimationEnd(Animation animation) {
                logo.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Start fade in immediately
        logo.startAnimation(fadeIn);

        // Schedule fade out after fade in
        new Handler().postDelayed(() -> {
            logo.startAnimation(fadeOut);
        }, 2000);
    }
}
