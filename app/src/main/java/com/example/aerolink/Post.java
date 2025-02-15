package com.example.aerolink;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Post {
    private String title;
    private String description;
    private String imageUrl;

    public Post() {} // Required empty constructor for Firestore

    public Post(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}