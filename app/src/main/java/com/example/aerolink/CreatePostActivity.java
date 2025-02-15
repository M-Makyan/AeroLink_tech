package com.example.aerolink;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText titleInput, descriptionInput;
    private ImageView selectedImage;
    private Uri imageUri;
    private Button uploadImageButton, postButton;
    private ProgressBar progressBar;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        selectedImage = findViewById(R.id.selectedImage);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        postButton = findViewById(R.id.postButton);
        progressBar = findViewById(R.id.progressBar);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        uploadImageButton.setOnClickListener(v -> openImagePicker());
        postButton.setOnClickListener(v -> createPost());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            selectedImage.setImageURI(imageUri);
            selectedImage.setVisibility(View.VISIBLE);
        }
    }

    private void createPost() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        postButton.setEnabled(false);
        uploadImageButton.setEnabled(false);

        if (imageUri != null) {
            uploadImageToFirebase(title, description, imageUri);
        } else {
            savePostToFirestore(title, description, null);
        }
    }

    private void uploadImageToFirebase(String title, String description, Uri imageUri) {
        StorageReference storageRef = storage.getReference().child("post_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                        savePostToFirestore(title, description, uri.toString())
                )
        ).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            postButton.setEnabled(true);
            uploadImageButton.setEnabled(true);
            Toast.makeText(CreatePostActivity.this, "Image upload failed! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void savePostToFirestore(String title, String description, String imageUrl) {
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        post.put("imageUrl", imageUrl);
        post.put("timestamp", System.currentTimeMillis()); // Add timestamp field

        firestore.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreatePostActivity.this, "Post uploaded!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreatePostActivity.this, "Failed to upload post!", Toast.LENGTH_SHORT).show();
                });
    }
}
