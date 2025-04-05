package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameSignUp, emailSignUp, passwordSignUp, confirmPasswordSignUp;
    private Button btnSignUp;
    private TextView txtLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        usernameSignUp = findViewById(R.id.usernameSignUp);
        emailSignUp = findViewById(R.id.emailSignUp);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        confirmPasswordSignUp = findViewById(R.id.confirmPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtLogin = findViewById(R.id.txtLogin);

        // Set click listener for the login text to go back to the LoginActivity
        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(v -> {
            String username = usernameSignUp.getText().toString().trim();
            String email = emailSignUp.getText().toString().trim();
            String password = passwordSignUp.getText().toString().trim();
            String confirmPassword = confirmPasswordSignUp.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if username already exists in Firestore
            db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Username already exists
                            Toast.makeText(RegisterActivity.this, "Username already exists. Please choose another one.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Proceed with registration
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()){
                                            FirebaseUser user = auth.getCurrentUser();
                                            if(user != null){
                                                // Update user profile with the username
                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(username)
                                                        .build();
                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(updateTask -> {
                                                            if(updateTask.isSuccessful()){
                                                                // Send verification email
                                                                user.sendEmailVerification()
                                                                        .addOnCompleteListener(emailTask -> {
                                                                            if(emailTask.isSuccessful()){
                                                                                // Save user data to Firestore
                                                                                Map<String, Object> userData = new HashMap<>();
                                                                                userData.put("username", username);
                                                                                userData.put("email", email);
                                                                                db.collection("users").document(user.getUid()).set(userData)
                                                                                        .addOnSuccessListener(aVoid -> {
                                                                                            Toast.makeText(RegisterActivity.this, "Registration successful. Verification email sent!", Toast.LENGTH_LONG).show();
                                                                                            // Redirect to LoginActivity after successful registration
                                                                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                                            finish();
                                                                                        })
                                                                                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show());
                                                                            } else {
                                                                                Toast.makeText(RegisterActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(RegisterActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Sign Up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error checking username", Toast.LENGTH_SHORT).show());
        });
    }
}