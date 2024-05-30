package com.hb.ecocollectte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QuerySnapshot;

public class login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword, editTextCode;
    Button signIn;
    TextView signUp;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String CORRECT_CODE = "COLLECTE1234";
    private static final int MAX_ATTEMPTS = 3;
    int attemptCount = 0;
    boolean isBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(ContextCompat.getColor(login.this, R.color.Green1));

        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.password);
        editTextCode = findViewById(R.id.code);
        signIn = findViewById(R.id.Sign_in);
        signUp = findViewById(R.id.Sign_up);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBlocked) {
                    Toast.makeText(login.this, "You are temporarily blocked. Please try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email, password, code;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                code = String.valueOf(editTextCode.getText());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(code)) {
                    Toast.makeText(login.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!code.equals(CORRECT_CODE)) {
                    attemptCount++;
                    if (attemptCount >= MAX_ATTEMPTS) {
                        isBlocked = true;
                        Toast.makeText(login.this, "Too many incorrect attempts. Please wait for 1 minute.", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                isBlocked = false;
                                attemptCount = 0;
                            }
                        }.start();
                    } else {
                        Toast.makeText(login.this, "Incorrect code. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }


                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        String storedPassword = document.getString("password");
                                        if (password.equals(storedPassword)) {
                                            Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(login.this, profile.class);
                                            intent.putExtra("username", document.getString("username"));
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", storedPassword);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(login.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }}