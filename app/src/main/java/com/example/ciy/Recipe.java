package com.example.ciy;


import java.io.Serializable;
import java.util.List;

/**
 * class representing a recipe
 */
public class Recipe implements Serializable {
    /* the id (sometimes we will need the global collection id)*/
    private String id;
    /* the recipe title */
    private String title;
    /* the recipe description */
    private String description;
    /* the recipe image url (if image missing default image will show)*/
    private String imageUrl;
    /* the recipe view counter */
    private int views;
    /* list of all the ingredients needed to make that recipe */
    private List<String> ingredients;

    public Recipe() {
        // public no-arg constructor necessary for Firestore
    }


    /**
     * constructor for or recipe
     *
     * @param title       the title
     * @param description the description
     * @param views       the ciew count
     * @param ingredients ingredients needed
     * @param imageUrl    the image url
     */
    Recipe(String title, String description, int views, List<String> ingredients, String imageUrl) {
        this.title = title;
        this.description = description;
        this.views = views;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    int getViews() {
        return views;
    }


    List<String> getIngredients() {
        return ingredients;
    }

    String getImageUrl() {
        return imageUrl;
    }
}
