package com.example.aerolink;

import com.google.firebase.firestore.GeoPoint;

public class Post {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String username;
    private long   timestamp;
    private GeoPoint location;    // <-- new field

    public Post() { }

    // Full constructor (optional)
    public Post(String title, String description, String imageUrl,
                String username, long timestamp, GeoPoint location) {
        this.title       = title;
        this.description = description;
        this.imageUrl    = imageUrl;
        this.username    = username;
        this.timestamp   = timestamp;
        this.location    = location;
    }

    // Getters & setters
    public String getId()               { return id; }
    public void setId(String id)        { this.id = id; }

    public String getTitle()            { return title; }
    public String getDescription()      { return description; }
    public String getImageUrl()         { return imageUrl; }
    public String getUsername()         { return username; }
    public long   getTimestamp()        { return timestamp; }

    public GeoPoint getLocation()       { return location; }
    public void     setLocation(GeoPoint location) {
        this.location = location;
    }
}