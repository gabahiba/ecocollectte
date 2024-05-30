package com.hb.ecocollectte;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends AppCompatActivity {

    private static final String TAG = "profile";

    CircleImageView userProfileImage;
    ImageView plusImageView;
    ImageView backImageView;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(ContextCompat.getColor(profile.this, R.color.Green1));


        userProfileImage = findViewById(R.id.userProfileImage);
        plusImageView = findViewById(R.id.plus);
        backImageView = findViewById(R.id.back);

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile.this, home.class);
                startActivity(intent);
            }
        });


        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        fetchUserInfo();
    }

    private void fetchUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String username = documentSnapshot.getString("username");
                    String email = documentSnapshot.getString("email");
                    String password = documentSnapshot.getString("password");


                    TextView usernameTextView1 = findViewById(R.id.usernameTextView1);
                    TextView usernameTextView2 = findViewById(R.id.usernameTextView2);
                    TextView emailTextView = findViewById(R.id.emailTextView);
                    TextView passwordTextView = findViewById(R.id.passwordTextView);

                    usernameTextView1.setText(username);
                    usernameTextView2.setText(username);
                    emailTextView.setText(email);
                    passwordTextView.setText(password);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error getting documents: ", e);
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                userProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
