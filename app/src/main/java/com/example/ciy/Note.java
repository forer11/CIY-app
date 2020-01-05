package com.example.ciy;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Note {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private int views;
    private List<String> ingredients;

    public Note() {
        // public no-arg constructor necessary for Firestore
    }

    public Note(String title, String description, int views) {
        this.title = title;
        this.description = description;
        this.views = views;
    }

    public Note(String title, String description, int views, String imageUrl) {
        this.title = title;
        this.description = description;
        this.views = views;
        this.imageUrl = imageUrl;
    }

    public Note(String title, String description, int views, List<String> ingredients, String imageUrl) {
        this.title = title;
        this.description = description;
        this.views = views;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
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

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
