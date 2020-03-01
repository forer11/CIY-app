package com.example.ciy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    static final int HOME = 1;
    static final int FAVORITES = 2;
    static final int SEARCH = 3;
    static final int DISCOVER = 4;

    static final int BOTTOM_NAV = 10;
    static final int SEARCH_RECIPE = 11;

    static final int NAME_FILTER = 1;
    static final int INGREDIENTS_FILTER = 2;

    static final boolean[] filterClickRecord = {false, false, false, false};
    static final int OTHER1 = 0, OTHER2 = 1, OTHER3 = 2, OTHER4 = 3;


    /* array list of all recipes shared by all activities*/
    static ArrayList<Recipe> searchRecipes = new ArrayList<>();
    /* array list of all ingredients shared by all activities */
    static ArrayList<String> allIngredients = new ArrayList<>();

    static ArrayList<String> ingredients = new ArrayList<>();


    static void setMatchForIngredients() {
        for (Recipe recipe : searchRecipes) {
            ArrayList<String> recipeIngredients = new ArrayList<>(recipe.getIngredients());
            ArrayList<String> myIngredients = new ArrayList<>(ingredients);
            recipeIngredients.retainAll(myIngredients);
            double match = 0;
            if (ingredients.size() > 0) {
                match = (double) recipeIngredients.size() / recipe.getIngredients().size();
            }
            searchRecipes.get(searchRecipes.indexOf(recipe)).setMatchFactor(match);
        }
    }

    static ArrayList<Recipe> orderByIngredientsMatch(ArrayList<Recipe> filteredArrayList) {
        SharedData.setMatchForIngredients();
        Comparator<Recipe> compareByMatch = (Recipe recipe1, Recipe recipe2)
                -> Double.compare(recipe1.getMatchFactor(), recipe2.getMatchFactor());
        Collections.sort(filteredArrayList, compareByMatch.reversed());
        return filteredArrayList;
    }

    static ArrayList<Recipe> orderAlphabetically(ArrayList<Recipe> filteredArrayList) {
        Comparator<Recipe> compareByMatch = (Recipe recipe1, Recipe recipe2)
                -> recipe1.getTitle().compareTo(recipe2.getTitle());
        Collections.sort(filteredArrayList, compareByMatch);
        return filteredArrayList;
    }
}
