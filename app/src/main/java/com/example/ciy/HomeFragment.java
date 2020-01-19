package com.example.ciy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
 * fragment representing our user home screen where he can see the top viewed recipes
 */
public class HomeFragment extends Fragment {
    private static int SPLASH_TIMEOUT = 4000;
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

    /* boolean for when we can click on the recyclerView items (when we load the data) */
    // TODO DECIDE IF NEEDED
    private FloatingActionButton addNoteButton;

    //TODO DELETE
    final String[] urls = new String[]{"https://boygeniusreport.files.wordpress.com/2016/11/puppy-dog.jpg?quality=98&strip=all&w=782",
            "https://images2.minutemediacdn.com/image/upload/c_crop,h_1350,w_2400,x_0,y_136/f_auto,q_auto,w_1100/v1576859350/shape/mentalfloss/610651-gettyimages-901452436.jpg",
            "https://cdn.psychologytoday.com/sites/default/files/styles/article-inline-half/public/field_blog_entry_images/2018-02/vicious_dog_0.png?itok=nsghKOHs",
            "https://scx2.b-cdn.net/gfx/news/hires/2019/wolfdog.jpg",
            "https://img.thedailybeast.com/image/upload/c_crop,d_placeholder_euli9k,h_1687,w_3000,x_0,y_0/dpr_1.5/c_limit,w_1044/fl_lossy,q_auto/v1575669519/191206-weill-dogs-in-politics-tease_ko5qke",
            "https://d.newsweek.com/en/full/1517827/coconut-rice-bear.jpg?w=1600&h=1600&q=88&f=8b37e38c82ec050dda787e009f0ef2ef",
            "https://compote.slate.com/images/8aedcaf8-0474-4644-b1b9-6a00220dc2dd.jpeg?width=780&height=520&rect=1560x1040&offset=0x0"};
    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        user = firebaseAuth.getCurrentUser();

        String userId;
        if (user != null) {
            userId = user.getUid();
        } else {
            userId = "guest";
        }
        setUpRecyclerView();
        View b = Objects.requireNonNull(getView()).findViewById(R.id.test);
        b.setVisibility(View.GONE);
        setClickListeners();
        recipeAdapter.startListening();
        //updateIngredientsVector();
        updateAllRecipes();
    }

    // updates the recipes with the json file TODO delete before submission
    public void updateAllRecipes() {
        try {
            JSONArray jsonRecipes = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonRecipes.length(); i++) {
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);
                String title = jsonRecipe.getString("name");
                String description = jsonRecipe.getString("description");
                String imageUrl = "https:" + jsonRecipe.getString("img_url");
                Random random = new Random();
                int views = random.nextInt(50000);
                JSONArray jsonIngredients = jsonRecipe.getJSONArray("new ingredients");
                List<String> ingredients = new ArrayList<>();
                for (int j = 0; j < jsonIngredients.length(); j++) {
                    ingredients.add(jsonIngredients.getString(j));
                }
                Recipe recipe = new Recipe(title, description, views, ingredients, imageUrl);
                recipe.setId(title);
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

    // important for the filtering process, TODO delete after filtering
    private void setUpData() {
//        usersRef.document(userId).collection("Recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    usersRef.document(userId).collection("Recipes").document(documentSnapshot.getId()).delete();
//                }
//                final ArrayList<Recipe> recipes = new ArrayList<>();
//                recipesRef.orderBy("views", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
//                            recipe.setId(documentSnapshot.getId());
//                            recipes.add(recipe);
//                            usersRef.document(userId).collection("Recipes").add(recipe);
//                        }
//                    }
//                });
//            }
//        });

//        addNoteButton = Objects.requireNonNull(getView()).findViewById(R.id.addButton);
//        addNoteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), NewNoteActivity.class));
//            }
//        });
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
        RecyclerView recyclerView = Objects.requireNonNull(getView())
                .findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recipeAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView
                    .ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                recipeAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }


    private void setClickListeners() {
        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                recipeAdapter.isClickable = false;
                final Recipe recipe = documentSnapshot.toObject(Recipe.class);
                //doggy related, keep it for now
//                    Random random = new Random();
//                    final int index = random.nextInt(urls.length);
//                    recipesRef.document(documentSnapshot.getId()).update("imageUrl", urls[index]);
                //update or create recipe fragment
                executeTransaction(documentSnapshot.getId(), recipesRef);

                setUpRecipeFragment(recipe);


            }

        });
    }


    private void setUpRecipeFragment(final Recipe recipe) {
        CollectionReference favoritesRef = usersRef.
                document(user.getUid()).collection(SharedData.Favorites);
        final DocumentReference favoriteRecipeRef = favoritesRef.document(recipe.getId());
        favoriteRecipeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean userPressedLike = false;
                if (!(documentSnapshot == null || !documentSnapshot.exists())) {
                    userPressedLike = true;
                }
                updatesRecipeFragment(recipe, userPressedLike);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("RestrictedApi") //TODO CARMEL
    private void updatesRecipeFragment(Recipe recipe, boolean userPressedLike) {
//        addNoteButton.setVisibility(View.INVISIBLE);
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, userPressedLike);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.recipePlaceholder, recipeFragment);
        fragmentTransaction.addToBackStack(null);
        // Complete the changes added above
        fragmentTransaction.commit();
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
        db.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference noteRef = dataCollection.document(id);
                DocumentSnapshot noteSnapShot = transaction.get(noteRef);
                long newViews = noteSnapShot.getLong("views") + 1;
                transaction.update(noteRef, "views", newViews);
                return newViews;

            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                //Toast.makeText(getActivity(), "Views updated to: " + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void enableClickables() {
        recipeAdapter.isClickable = true;
    }

}
