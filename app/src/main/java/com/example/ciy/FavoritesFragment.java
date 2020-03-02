package com.example.ciy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * This class represents the favorites fragment, that contains all the recipes the user has "liked"
 * (i.e - pressed like)
 */
public class FavoritesFragment extends Fragment {
    /* constants */
    private static final String FROM_FAVORITES_TAG = "RecipeFromFavorites";

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* reference to the individual user favorites collection */
    private CollectionReference favoritesRef;

    /* adapter to the Firestore recipes recyclerView */
    private RecipeAdapter recipeAdapter;
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // the current user in the firestore database
        FirebaseUser user = firebaseAuth.getCurrentUser();
        // the favorites reference to the firestore favorites collection
        assert user != null;
        favoritesRef = usersRef.document(user.getUid()).collection(SharedData.Favorites);

        setUpRecyclerView();
        setClickListeners();
    }


    /**
     * sets the RecyclerView for the Favorites Fragment, where we define the recipe Adapter.
     */
    private void setUpRecyclerView() {

        // sets the query we order the data with in the recyclerView
        Query queryStore = favoritesRef;
        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(queryStore, Recipe.class)
                .build();

        recipeAdapter = new RecipeAdapter(options);
        recipeAdapter.setLayout(R.layout.favorite_item);
        /* the recycler view object */
        RecyclerView recyclerView = Objects.requireNonNull(getView())
                .findViewById(R.id.favoritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recipeAdapter);
        recipeAdapter.startListening();
        setTouchLogic(recyclerView);
    }

    /**
     * configure the touch and swipe events, swiping will remove the recipe from the favorites
     *
     * @param recyclerView the recyclerView object
     */
    private void setTouchLogic(RecyclerView recyclerView) {
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

    /**
     * configure the click event, when clicked we will open the recipe page.
     */
    private void setClickListeners() {
        recipeAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            recipeAdapter.isClickable = false;
            final Recipe recipe = documentSnapshot.toObject(Recipe.class);
            showRecipeFragment(recipe);
        });
    }

    /**
     * opens the recipe page for the recipe we clicked on
     *
     * @param recipe the clicked recipe
     */
    private void showRecipeFragment(Recipe recipe) {
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, true,
                SharedData.BOTTOM_NAV);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager();
        recipeFragment.show(fragmentManager, FROM_FAVORITES_TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // stops the listener.
        recipeAdapter.stopListening();
    }

    /**
     * enable click events after returning from the recipe page.
     */
    void enableClickable() {
        recipeAdapter.isClickable = true;
    }
}
