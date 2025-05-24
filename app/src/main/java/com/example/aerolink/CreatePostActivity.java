// src/main/java/com/example/aerolink/CreatePostActivity.java
package com.example.aerolink;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private ImageView selectedImage;
    private Uri imageUri;
    private Switch locationSwitch;
    private Button uploadImageButton, postButton;
    private ProgressBar progressBar;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    // Launcher for the system image picker
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            imageUri = uri;
                            selectedImage.setImageURI(uri);
                            selectedImage.setVisibility(ImageView.VISIBLE);
                        }
                    }
            );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Wire views
        titleInput         = findViewById(R.id.titleInput);
        descriptionInput   = findViewById(R.id.descriptionInput);
        selectedImage      = findViewById(R.id.selectedImage);
        locationSwitch     = findViewById(R.id.locationSwitch);
        uploadImageButton  = findViewById(R.id.uploadImageButton);
        postButton         = findViewById(R.id.postButton);
        progressBar        = findViewById(R.id.progressBar);

        // Firebase instances
        storage            = FirebaseStorage.getInstance();
        firestore          = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Image picker
        uploadImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Optional: fetch location if switch enabled
        locationSwitch.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(loc -> userLocation = loc)
                        .addOnFailureListener(e -> {
                            locationSwitch.setChecked(false);
                            Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Post button
        postButton.setOnClickListener(v -> createPost());
    }

    private void createPost() {
        String title = titleInput.getText().toString().trim();
        String desc  = descriptionInput.getText().toString().trim();
        if (title.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Title & description required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        postButton.setEnabled(false);
        uploadImageButton.setEnabled(false);

        if (imageUri != null) {
            uploadImageToFirebase(title, desc, imageUri);
        } else {
            savePostToFirestore(title, desc, null);
        }
    }

    private void uploadImageToFirebase(String title, String description, Uri imageUri) {
        StorageReference storageRef = storage.getReference()
                .child("post_images/" + System.currentTimeMillis() + ".jpg");

        Log.d("UploadDebug", "Uploading to path: " + storageRef.getPath());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d("UploadDebug", "Download URL: " + uri);
                                savePostToFirestore(title, description, uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UploadDebug", "getDownloadUrl failed", e);
                                resetUIWithError("Failed to retrieve image URL");
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("UploadDebug", "putFile failed", e);
                    resetUIWithError("Image upload failed: " + e.getMessage());
                });
    }

    private void savePostToFirestore(String title, String description, @Nullable String imageUrl) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String username = (user != null && user.getDisplayName() != null)
                ? user.getDisplayName()
                : (user != null ? user.getEmail() : "Unknown");

        Map<String,Object> data = new HashMap<>();
        data.put("title", title);
        data.put("description", description);
        data.put("imageUrl", imageUrl);
        data.put("username", username);
        data.put("timestamp", System.currentTimeMillis());
        if (locationSwitch.isChecked() && userLocation != null) {
            data.put("location", new GeoPoint(
                    userLocation.getLatitude(),
                    userLocation.getLongitude()
            ));
        }

        firestore.collection("posts")
                .add(data)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> resetUIWithError("Failed to save post: " + e.getMessage()));
    }

    private void resetUIWithError(String msg) {
        progressBar.setVisibility(ProgressBar.GONE);
        postButton.setEnabled(true);
        uploadImageButton.setEnabled(true);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}