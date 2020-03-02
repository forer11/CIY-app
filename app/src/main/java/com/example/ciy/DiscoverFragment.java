package com.example.ciy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * This fragment represents the discover screen were the user can see the top viewed recipes
 */
public class DiscoverFragment extends Fragment {

    /* when user press the discover button (when already there) if scrolls up fast to the 10'th
     * view, then activate animation */
    private static final int FAST_SCROLL_POSITION = 10;
    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* reference to the firestore global ingredients collection */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);

    /* adapter to the Firestore recipes recyclerView */
    private RecipeAdapter recipeAdapter;
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /* the recycler view object */
    private RecyclerView recyclerView;
    /* the current user registered to our database */
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        user = firebaseAuth.getCurrentUser();
        setUpRecyclerView();
        setClickListeners();
        recipeAdapter.startListening();
//        updateIngredientsVector();
//        updateAllRecipes();
    }

    // updates the recipes with the json file TODO delete before submission
    public void updateAllRecipes() {
        try {
            JSONArray jsonRecipes = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonRecipes.length(); i++) {
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);
                String title = jsonRecipe.getString("name");
                String description = jsonRecipe.getString("description");

                StringBuilder instructions = new StringBuilder();
                List<String> instructionsParts = new ArrayList<>();
                JSONArray jsonMethod = jsonRecipe.getJSONArray("method");
                for (int j = 0; j < jsonMethod.length(); j++) {
                    instructionsParts.add(jsonMethod.getString(j));
                    instructions.append(jsonMethod.getString(j));
                    instructions.append("\n");
                }

                JSONArray jsonTime = jsonRecipe.getJSONArray("time");
                JSONObject obj = jsonTime.getJSONObject(0);
                JSONObject obj2 = obj.getJSONObject("cook");
                String hours = obj2.getString("hrs");
                if (!hours.equals("null")) {
                    hours = hours.substring(0, hours.indexOf(" "));
                } else {
                    hours = "0";
                }
                int h = Integer.parseInt(hours);
                String minutes = obj2.getString("mins");
                if (!minutes.equals("null")) {
                    minutes = minutes.substring(0, minutes.indexOf(" "));
                } else {
                    minutes = "0";
                }
                int m = Integer.parseInt(minutes);
                int t = (h * 60) + m;
                String time = String.valueOf(t);

                obj = jsonRecipe.getJSONObject("nutrition");
                String calories = obj.getString("kcal");
                String protein = obj.getString("protein");


                String imageUrl = "https:" + jsonRecipe.getString("img_url");
                String difficulty = jsonRecipe.getJSONArray("difficulty").getString(0);

                Random random = new Random();
                int views = random.nextInt(1000000);
                JSONArray jsonIngredients = jsonRecipe.getJSONArray("new ingredients");
                List<String> ingredients = new ArrayList<>();
                for (int j = 0; j < jsonIngredients.length(); j++) {
                    ingredients.add(jsonIngredients.getString(j));
                }
                List<String> extendedIngredients = new ArrayList<>();
                JSONArray jsonUserIngredients = jsonRecipe.getJSONArray("ingredients");
                for (int j = 0; j < jsonUserIngredients.length(); j++) {
                    extendedIngredients.add(jsonUserIngredients.getString(j));
                }
                Recipe recipe = new Recipe(title, description, views, ingredients, imageUrl);
                recipe.setInstructions(instructions.toString());
                recipe.setExtendedIngredients(extendedIngredients);
                recipe.setDifficulty(difficulty);
                recipe.setId(title);
                recipe.setPreparationTime(time);
                recipe.setCalories(calories);
                recipe.setProtein(protein);
                recipe.setInstructionsParts(instructionsParts);
                recipesRef.document(title).set((recipe), SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });


                //Add your values in your `ArrayList` as below:

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // load the json file TODO delete before submission

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = Objects.requireNonNull(getActivity()).getAssets().open("DB.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // updates the ingredients vector by text file in assets TODO delete before submission
    private void updateIngredientsVector() {

        try {
            InputStream is = Objects.requireNonNull(getContext()).getAssets().open("ingredients.txt");
            StringBuilder text = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("ingredient", line);
                ingredientsRef.document(line).set(data, SetOptions.merge());
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    /**
     * set up the recyclerView configurations including the adapter and query.
     */
    private void setUpRecyclerView() {

        // sets the query we order the data in the recyclerView by
        Query query = recipesRef.orderBy("views", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .build();

        recipeAdapter = new RecipeAdapter(options);
        recipeAdapter.setLayout(R.layout.recipe_item);
        recyclerView = Objects.requireNonNull(getView())
                .findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recipeAdapter);
    }


    private void setClickListeners() {
        recipeAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            recipeAdapter.isClickable = false;
            final Recipe recipe = documentSnapshot.toObject(Recipe.class);
            // increments the views for a recipe if entered.
            executeTransaction(documentSnapshot.getId(), recipesRef);
            // sets up the recipe page and open it
            setUpRecipeFragment(Objects.requireNonNull(recipe));
        });
    }

    /**
     * sets the data needed to open the recipe page
     *
     * @param recipe the clicked recipe
     */
    private void setUpRecipeFragment(final Recipe recipe) {
        CollectionReference favoritesRef = usersRef.
                document(user.getUid()).collection(SharedData.Favorites);
        final DocumentReference favoriteRecipeRef = favoritesRef.document(recipe.getId());
        favoriteRecipeRef.get().addOnSuccessListener(documentSnapshot -> {
            // checks if the recipe is one of the user's favorites
            boolean userPressedLike = false;
            if (!(documentSnapshot == null || !documentSnapshot.exists())) {
                userPressedLike = true;
            }
            openRecipeFragment(recipe, userPressedLike);
        }).addOnFailureListener(e -> Toast.makeText(getContext(),
                "Failed to load data", Toast.LENGTH_SHORT).show());
    }

    /**
     * opens the recipe page for the recipe the user clicked on
     *
     * @param recipe          the clicked recipe
     * @param userPressedLike if the recipe is in the user's favorites: true, otherwise: false.
     */
    private void openRecipeFragment(Recipe recipe, boolean userPressedLike) {
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, userPressedLike,
                SharedData.BOTTOM_NAV);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager();
        recipeFragment.show(fragmentManager, "RecipeFromDiscover");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recipeAdapter.stopListening();
    }

    /**
     * * incrementing a parameter in fireStore with synchronization
     *
     * @param id             the recipe id
     * @param dataCollection the data Collection in firestore
     */
    private void executeTransaction(final String id, final CollectionReference dataCollection) {
        db.runTransaction(transaction -> {
            DocumentReference noteRef = dataCollection.document(id);
            DocumentSnapshot noteSnapShot = transaction.get(noteRef);
            long newViews = noteSnapShot.getLong("views") + 1;
            transaction.update(noteRef, "views", newViews);
            return newViews;

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update Views",
                Toast.LENGTH_SHORT).show());
    }

    /**
     * we call this function from the fragment we opened to enable the Recycler view
     */
    void enableClickable() {
        recipeAdapter.isClickable = true;
    }

    /**
     * scroll to the top of the recycler view when we double press home
     */
    void scrollToTop() {
        int position = ((LinearLayoutManager) Objects.
                requireNonNull(recyclerView.getLayoutManager())).findFirstVisibleItemPosition();
        if (position > FAST_SCROLL_POSITION) {
            recyclerView.getLayoutManager().scrollToPosition(FAST_SCROLL_POSITION);
        }
        recyclerView.smoothScrollToPosition(0);

    }
}
