package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable callback;

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // make a copy of our recipes asynchronously
        loadRecipeCopy();

        //launch the next screen after delay
        //appStartHandler();
    }

    private void appStartHandler() {
        handler = new Handler();
        callback = new Runnable() {
            @Override
            public void run() {


            }
        };
        handler.postDelayed(callback, 1000);
    }

    private void loadRecipeCopy() {
        recipesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // sometimes having duplicates, didn't figure why, for now i will try this.
                SharedData.searchRecipes.clear();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Recipe recipe = documentSnapshot.toObject(Recipe.class);
                    SharedData.searchRecipes.add(recipe);
                }
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent;
                if (currentUser != null) {
                    intent = new Intent(getBaseContext(), BottomNavigationBar.class);
                } else {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // handler.removeCallbacks(callback);
    }
}
