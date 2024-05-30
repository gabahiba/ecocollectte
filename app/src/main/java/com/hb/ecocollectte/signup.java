package com.hb.ecocollectte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String CORRECT_CODE = "COLLECTE1234";
    private static final int MAX_ATTEMPTS = 3;

    TextInputEditText editTextEmail, editTextUsername, editTextCode;
    TextInputEditText editTextPassword;
    Button signUp;
    TextView signIn;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    int attemptCount = 0;
    boolean isBlocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setStatusBarColor(ContextCompat.getColor(signup.this, R.color.Green1));

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUsername = findViewById(R.id.username);
        editTextCode = findViewById(R.id.code);
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);

        TextInputLayout passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, login.class);
                startActivity(intent);
                finish();
            }
        });

        passwordTextInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBlocked) {
                    Toast.makeText(signup.this, "You are temporarily blocked. Please try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String code = editTextCode.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(code)) {
                    Toast.makeText(signup.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!code.equals(CORRECT_CODE)) {
                    attemptCount++;
                    if (attemptCount >= MAX_ATTEMPTS) {
                        isBlocked = true;
                        Toast.makeText(signup.this, "Too many incorrect attempts. Please wait for 1 minute.", Toast.LENGTH_SHORT).show();
                        new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                // Can be used to show a countdown timer if needed
                            }

                            @Override
                            public void onFinish() {
                                isBlocked = false;
                                attemptCount = 0;
                            }
                        }.start();
                    } else {
                        Toast.makeText(signup.this, "Incorrect code. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username)
                                            .build();

                                    if (user != null) {
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // Username successfully updated
                                                        }
                                                    }
                                                });

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("username", username);
                                        userData.put("email", email);
                                        userData.put("password", password);

                                        db.collection("users").document(user.getUid())
                                                .set(userData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error writing document", e);
                                                    }
                                                });
                                    }

                                    Toast.makeText(signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(signup.this, home.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(signup.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
