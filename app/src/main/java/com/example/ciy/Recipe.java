package com.example.ciy;


import java.io.Serializable;
import java.util.ArrayList;
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
    /* list of all the ingredients needed to make that recipe*/
    private List<String> ingredients;
    /* ingredients for user view */
    private List<String> extendedIngredients;
    /* instructions for how to make the dish */
    private String instructions;
    /* the difficulty of said recipe */
    private List<String> instructionsParts;
    private String difficulty;
    /* the preparation time */
    private String preparationTime;
    /* the recipe difficulty */

    /* the recipe calories */
    private String calories;

    /* the recipe proteins in grams*/
    private String protein;

    /* how the recipe matches with the user's ingredients*/
    private double matchFactor = 0;

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

    //TODO - for now i added another constructor -we need to delete one of them

    /**
     * constructor for or recipe
     *
     * @param title                   the title
     * @param description             the description
     * @param preparationTime         the preparation Time
     * @param preparationInstructions the preparation Instructions
     * @param ingredients             ingredients needed
     * @param imageUrl                the image url
     */
    Recipe(String title, String description, String preparationTime, String preparationInstructions,
           List<String> ingredients, String imageUrl) {
        this.title = title;
        this.description = description;
        this.preparationTime = preparationTime;
        this.instructions = preparationInstructions;
        this.ingredients = ingredients;
        this.imageUrl = imageUrl;
        this.views = 0; //new recipe
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getViews() {
        return views;
    }


    public List<String> getIngredients() {
        return ingredients;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<String> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public double getMatchFactor() {
        return matchFactor;
    }

    public void setMatchFactor(double matchFactor) {
        this.matchFactor = matchFactor;
    }

    public List<String> getInstructionsParts() {
        return instructionsParts;
    }

    public void setInstructionsParts(List<String> instructionsParts) {
        this.instructionsParts = instructionsParts;
    }
}
