package com.example.ciy;

import com.google.firebase.firestore.Exclude;

import java.util.Map;

public class Note {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private int views;
    private Map<String, Boolean> ingredients;

    public Note() {
        // public no-arg constructor necessary for Firestore
    }

    public Note(String title, String description, int views) {
        this.title = title;
        this.description = description;
        this.views = views;
    }
    public Note(String title, String description, int views,String imageUrl) {
        this.title = title;
        this.description = description;
        this.views = views;
        this.imageUrl = imageUrl;
    }

    public Note(String title, String description, int views, Map<String, Boolean> ingredients) {
        this.title = title;
        this.description = description;
        this.views = views;
        this.ingredients = ingredients;
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

    public Map<String, Boolean> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, Boolean> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
