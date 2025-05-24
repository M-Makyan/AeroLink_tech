package com.example.aerolink;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Post {
    private String id;           // ← new
    private String title;
    private String description;
    private String imageUrl;
    private String username;
    private long   timestamp;

    public Post() { }

    // optional full constructor
    public Post(String title, String description, String imageUrl,
                String username, long timestamp) {
        this.title       = title;
        this.description = description;
        this.imageUrl    = imageUrl;
        this.username    = username;
        this.timestamp   = timestamp;
    }

    // Firestore needs this setter when you do doc.toObject(Post.class)
    public void setId(String id) { this.id = id; }
    public String getId()        { return id; }

    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getImageUrl()    { return imageUrl; }
    public String getUsername()    { return username; }
    public long   getTimestamp()   { return timestamp; }
}