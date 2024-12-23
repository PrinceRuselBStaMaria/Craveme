package com.example.acc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
            logo.startAnimation(fadeIn);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            logo.startAnimation(fadeIn);

            new Handler().postDelayed(() -> {
                logo.startAnimation(fadeOut);
            }, 2000); // 2 seconds for fade in
        }
    }
