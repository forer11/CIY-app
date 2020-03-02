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
import androidx.recyclerview.widget.ItemTouchHelper;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        favoritesRef = usersRef.document(user.getUid()).collection(SharedData.Favorites);
        setUpRecyclerView();
        setClickListeners();
    }


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

    private void setClickListeners() {
        recipeAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            recipeAdapter.isClickable = false;
            final Recipe recipe = documentSnapshot.toObject(Recipe.class);
            updatesRecipeFragment(recipe);
        });
    }


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

    void enableClickable() {
        recipeAdapter.isClickable = true;
    }
}
