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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * This fragment represents the discover screen were the user can see the top viewed recipes
 */
public class DiscoverFragment extends Fragment {

    /* constants */
    private static final String LOAD_FAILURE_MSG = "Failed to load data";

    /* when user press the discover button (when already there) if scrolls up fast to the 10'th
     * view, then activate animation */
    private static final int FAST_SCROLL_POSITION = 10;
    private static final String FROM_DISCOVER_TAG = "RecipeFromDiscover";
    private static final String VIEWS_ERR_MSG = "Failed to update Views";

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);

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
                .findViewById(R.id.discoverRecyclerView);
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
                LOAD_FAILURE_MSG, Toast.LENGTH_SHORT).show());
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
        recipeFragment.show(fragmentManager, FROM_DISCOVER_TAG);
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

        }).addOnFailureListener(e -> Toast.makeText(getContext(), VIEWS_ERR_MSG,
                Toast.LENGTH_SHORT).show());
    }

    /**
     * we call this function from the fragment we opened to enable the Recycler view
     */
    void enableClickable() {
        recipeAdapter.isClickable = true;
    }

    /**
     * scroll to the top of the recycler view when we double press the discover icon
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
