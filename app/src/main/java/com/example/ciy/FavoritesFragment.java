package com.example.ciy;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class FavoritesFragment extends Fragment {
    private static final int FAST_SCROLL_POSITION = 10;
    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* reference to the firestore global ingredients collection */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);
    /* reference to the individual user favorites collection */
    private CollectionReference favoritesRef;

    /* adapter to the Firestore recipes recyclerView */
    private RecipeAdapter recipeAdapter;
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /* the recycler view object */
    private RecyclerView recyclerView;

    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        user = firebaseAuth.getCurrentUser();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        favoritesRef = usersRef.document(user.getUid()).collection(SharedData.Favorites);
        setUpRecyclerView();
        setClickListeners();
    }


    private void setUpRecyclerView() {

        //TODO get the recipes with the id from favorites
        // sets the query we order the data in the recyclerView by

        Query queryStore = favoritesRef;
        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(queryStore, Recipe.class)
                .build();

        recipeAdapter = new RecipeAdapter(options);
        recipeAdapter.setLayout(R.layout.favorite_item);
        recyclerView = Objects.requireNonNull(getView())
                .findViewById(R.id.favoritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recipeAdapter);
        recipeAdapter.startListening();
        //TODO set touch logic if we need to, for now commented out
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView
//                    .ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                //recipeAdapter.deleteItem(viewHolder.getAdapterPosition());
//            }
//        }).attachToRecyclerView(recyclerView);
    }

    private void setClickListeners() {
        recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                recipeAdapter.isClickable = false;
                final Recipe recipe = documentSnapshot.toObject(Recipe.class);
                updatesRecipeFragment(recipe);
            }
        });
    }


    @SuppressLint("RestrictedApi") //TODO CARMEL
    private void updatesRecipeFragment(Recipe recipe) {
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, true,
                SharedData.BOTTOM_NAV);
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager();
        recipeFragment.show(fragmentManager, "RecipeFromFavorites");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recipeAdapter.stopListening();
    }

    /**
     * indicates if we opened a recipe
     *
     * @return true if a recipe is opened, false otherwise
     */
    boolean isRecipeCurrentlyOpen() {
        return !recipeAdapter.isClickable;
    }

    // Currently have no use, do we want same behavior as Home? TODO Lior
    public void refreshData() {
        if (recipeAdapter != null) {
            recipeAdapter.stopListening();
        }
        setUpRecyclerView();
    }

    void enableClickable() {
        recipeAdapter.isClickable = true;
    }
}
