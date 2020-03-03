package com.example.ciy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);
    /* reference to the firestore global ingredients collection */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // make a copy of our recipes asynchronously
        loadRecipeCopy();
    }

    /**
     * loads the recipes and ingredients from the firestore database
     */
    private void loadRecipeCopy() {
        recipesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            SharedData.searchRecipes.clear();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Recipe recipe = documentSnapshot.toObject(Recipe.class);
                SharedData.searchRecipes.add(recipe);
            }
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent;
            if (currentUser != null) {
                intent = new Intent(getBaseContext(), BottomNavigationBarActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            }
            ingredientsRef.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                //we add all ingredients from our data base to 'ingredientOptions' list
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots1) {
                    String option = Objects
                            .requireNonNull(documentSnapshot.get("ingredient")).toString();
                    SharedData.allIngredients.add(option);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }).addOnFailureListener(e -> {
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
