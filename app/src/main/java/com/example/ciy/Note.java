package com.example.ciy;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String id;
    private String title;
    private String description;
    private int priority;

    public Note() {
        // public no-arg constructor necessary for Firestore
    }

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // meaning  that id won't be in the document.
    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
