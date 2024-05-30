package com.hb.ecocollectte;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class getstarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);
        getWindow().setStatusBarColor(ContextCompat.getColor(getstarted.this, R.color.Green1));


        Button buttonLogin = findViewById(R.id.button2);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getstarted.this, login.class);


                startActivity(intent);
            }
        });


        Button buttonSignup = findViewById(R.id.button3);


        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getstarted.this, signup.class);


                startActivity(intent);
            }
        });
    }
}