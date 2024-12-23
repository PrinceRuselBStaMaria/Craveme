package com.example.acc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView named;
    private Button buttonGoToJavalysus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        TextView welcomeText = findViewById(R.id.welcomeText);
        named = findViewById(R.id.named);
        buttonGoToJavalysus = findViewById(R.id.buttonGoToJavalysus);

        // Get the intent data
        String fullName = getIntent().getStringExtra("fullName");

        // Add null check and set text
        if (fullName != null && !fullName.isEmpty()) {
            named.setText(fullName + "!");
        } else {
            named.setText("No name provided");
        }

        // Set the button click listener
        buttonGoToJavalysus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity3.this, Javalysus.class);
                startActivity(intent);
            }
        });
    }
}