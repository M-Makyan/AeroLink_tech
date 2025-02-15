package com.example.aerolink;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private ProgressDialog progressDialog;

    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        selectedImage = findViewById(R.id.selectedImage);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        postButton = findViewById(R.id.postButton);
        progressDialog = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("post_images");

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

        progressDialog.setMessage("Posting...");
        progressDialog.show();

        if (imageUri != null) {
            uploadImageToFirebase(title, description);
        } else {
            savePostToFirestore(title, description, null);
        }
    }

    private void uploadImageToFirebase(String title, String description) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() + ".jpg");
        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> savePostToFirestore(title, description, uri.toString())))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }

    private void savePostToFirestore(String title, String description, String imageUrl) {
        Map<String, Object> post = new HashMap<>();
        post.put("title", title);
        post.put("description", description);
        if (imageUrl != null) post.put("imageUrl", imageUrl);

        db.collection("posts").add(post)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Post created successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(CreatePostActivity.this, "Failed to create post", Toast.LENGTH_SHORT).show();
                });
    }
}