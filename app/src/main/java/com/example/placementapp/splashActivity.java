package com.example.placementapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private String textToDisplay = "Placeroo";
    private int index = 0;
    TextView splashtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        splashtext = (TextView) findViewById(R.id.splashText);
        animateText();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(splashActivity.this, SignUp.class);
            startActivity(intent);
            finish();
        },SPLASH_DISPLAY_LENGTH);
    }
    private void animateText() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (index < textToDisplay.length()) {
                    splashtext.setText(textToDisplay.substring(0, index + 1));
                    index++;
                    handler.postDelayed(this, 100); // Adjust the delay to speed up/slow down animation
                }
            }
        };
        handler.post(runnable);
    }
}