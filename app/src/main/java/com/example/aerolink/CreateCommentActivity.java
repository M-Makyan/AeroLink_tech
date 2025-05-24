package com.example.aerolink;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateCommentActivity extends AppCompatActivity {
    public static final String EXTRA_POST_ID = "postId";

    private EditText commentInput;
    private Button btnPostComment;
    private ImageButton btnBack;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        // Initialize Firebase services
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Wire up views
        commentInput   = findViewById(R.id.commentInput);
        btnPostComment = findViewById(R.id.btnPostComment);
        btnBack        = findViewById(R.id.btnBack);

        // Back button closes this screen
        btnBack.setOnClickListener(v -> finish());

        // Retrieve the post ID passed in the Intent
        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId == null) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // When “Post Comment” is tapped
        btnPostComment.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (text.isEmpty()) {
                commentInput.setError("Please enter a comment");
                return;
            }

            // Determine the author name from the logged-in user
            FirebaseUser user = auth.getCurrentUser();
            String author = "Unknown";
            if (user != null) {
                if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                    author = user.getDisplayName();
                } else if (user.getEmail() != null) {
                    author = user.getEmail();
                }
            }

            long timestamp = System.currentTimeMillis();
            Comment comment = new Comment(author, text, timestamp);

            // Save into Firestore under posts/{postId}/comments
            firestore.collection("posts")
                    .document(postId)
                    .collection("comments")
                    .add(comment)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
                        finish();  // return to detail screen
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show()
                    );
        });
    }
}