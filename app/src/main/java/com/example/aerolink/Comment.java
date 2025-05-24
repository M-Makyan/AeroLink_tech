package com.example.aerolink;

public class Comment {
    private String id;
    private String author;
    private String text;
    private long timestamp;

    // Firestore requires a public no-arg constructor
    public Comment() { }

    // This is the constructor you're calling
    public Comment(String author, String text, long timestamp) {
        this.author    = author;
        this.text      = text;
        this.timestamp = timestamp;
    }

    // getters & setters...
    public String getId()             { return id; }
    public void   setId(String id)    { this.id = id; }
    public String getAuthor()         { return author; }
    public void   setAuthor(String a) { this.author = a; }
    public String getText()           { return text; }
    public void   setText(String t)   { this.text = t; }
    public long   getTimestamp()      { return timestamp; }
    public void   setTimestamp(long ts){ this.timestamp = ts; }
}