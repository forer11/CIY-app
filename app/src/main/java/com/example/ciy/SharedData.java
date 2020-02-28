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
    static final int DISCOVER = 4;

    static final int BOTTOM_NAV = 10;
    static final int SEARCH_RECIPE = 11;

    static final int NAME_FILTER = 1;
    static final int INGREDIENTS_FILTER = 2;


    /* array list of all recipes shared by all activities*/
    static ArrayList<Recipe> searchRecipes = new ArrayList<>();
    /* array list of all ingredients shared by all activities */
    static ArrayList<String> allIngredients = new ArrayList<>();

    static ArrayList<String> ingredients = new ArrayList<>();
}
