package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class SearchRecipeActivity extends AppCompatActivity {

    /* the search recyclerView adapter */
    private SearchAdapter searchAdapter;

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = firebaseAuth.getCurrentUser();

        setContentView(R.layout.activity_search_recipe);
        final ArrayList<Recipe> searchRecipes = new ArrayList<>();

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView(searchRecipes);

        setUpAdapterListeners(searchRecipes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearchNavigation);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // will not enable search click, only realtime search //TODO Lior
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setIconifiedByDefault(false);


        setUpSearchViewListeners(searchItem, searchView);
        return true;
    }

    private void setUpSearchViewListeners(MenuItem searchItem, SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return true;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        });
    }

    private void setUpAdapterListeners(final ArrayList<Recipe> searchRecipes) {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
//                searchRecipes.get(position).setId("wichongo");
//                searchAdapter.notifyItemChanged(position);
                setUpRecipeFragment(searchRecipes.get(position));
            }

            @Override
            public void OnLikeClick(int position) {
                searchRecipes.remove(position);
                searchAdapter.notifyItemRemoved(position);
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
                Toast.makeText(SearchRecipeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatesRecipeFragment(Recipe recipe, boolean userPressedLike) {
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, userPressedLike
                , SharedData.SEARCH_RECIPE);
        FragmentManager fragmentManager = Objects.requireNonNull(SearchRecipeActivity.this)
                .getSupportFragmentManager();
        recipeFragment.show(fragmentManager, "RecipeFromSearchRecipe");
    }

    private void setUpRecyclerView(ArrayList<Recipe> searchRecipes) {
        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchAdapter = new SearchAdapter(searchRecipes);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }
}
