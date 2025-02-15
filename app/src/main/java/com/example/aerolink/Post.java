package com.example.aerolink;

import android.net.Uri;

public class Post {
    private String title;
    private String description;
    private Uri imageUri;

    // Constructor accepting String (for Firebase retrieval)
    public Post(String title, String description, String imageUri) {
        this.title = title;
        this.description = description;
        this.imageUri = (imageUri != null && !imageUri.isEmpty()) ? Uri.parse(imageUri) : null;
    }

    // Constructor accepting Uri
    public Post(String title, String description, Uri imageUri) {
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}