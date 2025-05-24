package com.example.aerolink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin, passwordLogin;
    private Button btnDemoAccount, btnLogin;
    private TextView txtSignUp;
    private FirebaseAuth mAuth;

    // Demo credentials
    private static final String DEMO_EMAIL    = "individualproject2025@gmail.com";
    private static final String DEMO_PASSWORD = "Samsung2025";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailLogin    = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnDemoAccount= findViewById(R.id.btnDemoAccount);
        btnLogin      = findViewById(R.id.btnLogin);
        txtSignUp     = findViewById(R.id.txtSignUp);

        // Autofill demo credentials
        btnDemoAccount.setOnClickListener(v -> {
            emailLogin.setText(DEMO_EMAIL);
            passwordLogin.setText(DEMO_PASSWORD);
        });

        btnLogin.setOnClickListener(v -> {
            String email    = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String username = user != null
                                    ? user.getDisplayName()
                                    : "Unknown User";

                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        txtSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}