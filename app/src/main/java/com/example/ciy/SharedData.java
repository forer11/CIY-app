package com.example.ciy;

import java.util.ArrayList;

/**
 * the App Shared data
 */
class SharedData {
    /* Shared constant Strings */
    static final String RECIPES = "Recipes";
    static final String USERS = "Users";
    static final String Ingredients = "Ingredients";
    static final String Favorites = "Favorites";
    /* shared constant Integers */
    static final int DEFAULT = 0;
    static final int HOME =1;
    static final int FAVORITES =2;
    static final int SEARCH = 3;

    /* array list of all recipes shared by all activities*/
    static ArrayList<Recipe> searchRecipes = new ArrayList<>();
    /* array list of all ingredients shared by all activities */
    static ArrayList<String> allIngredients = new ArrayList<>();
}
