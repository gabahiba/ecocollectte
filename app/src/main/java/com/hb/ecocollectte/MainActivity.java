package com.hb.ecocollectte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        logo = findViewById(R.id.image);


        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.green));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateViews();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(MainActivity.this, getstarted.class));
                        finish();
                    }
                }, 1000);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void animateViews() {
        ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f).setDuration(1000).start();
    }
}
