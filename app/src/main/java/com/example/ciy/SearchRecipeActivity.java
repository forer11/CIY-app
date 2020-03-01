package com.example.ciy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class SearchRecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Recipe> searchRecipes;

    private enum Filters {
        ALL, INGREDIENTS;
    }


    /* the search recyclerView adapter */
    private SearchAdapter searchAdapter;

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseUser user;

    private Button filterAll, filterByIngredients;

    private Button filterBycalories, filterByProtein, filterByTime, filterByDifficult;

    private ArrayList<Button> filterButtons;

    private Filters currFilter;

    private String searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);

        user = firebaseAuth.getCurrentUser();
        currFilter = Filters.ALL;
        configureFilterButtons();
        setupFilterListener();
        filterAll.setBackgroundResource(R.drawable.filter_button_pressed);

        searchRecipes = new ArrayList<>(SharedData.searchRecipes);

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView();

        setUpAdapterListeners();

    }

    private void setupFilterListener() {
        handelAllFilter();

        handelByIngredientsFilter();

        filterBycalories.setOnClickListener(v -> {
            if (!SharedData.filterClickRecord[SharedData.LOW_CALORIES]) {
                v.setBackgroundResource(R.drawable.filter_button_pressed2);
                SharedData.filterClickRecord[SharedData.LOW_CALORIES] = true;
            } else {
                v.setBackgroundResource(R.drawable.filter_button_unpressed);
                SharedData.filterClickRecord[SharedData.LOW_CALORIES] = false;

            }
            activateFilter();
        });

        filterByProtein.setOnClickListener(v -> {
            if (!SharedData.filterClickRecord[SharedData.HIGH_PROTEIN]) {
                v.setBackgroundResource(R.drawable.filter_button_pressed2);
                SharedData.filterClickRecord[SharedData.HIGH_PROTEIN] = true;
            } else {
                v.setBackgroundResource(R.drawable.filter_button_unpressed);
                SharedData.filterClickRecord[SharedData.HIGH_PROTEIN] = false;
            }
            activateFilter();
        });
        filterByTime.setOnClickListener(v -> {
            if (!SharedData.filterClickRecord[SharedData.SHORT_TIME]) {
                v.setBackgroundResource(R.drawable.filter_button_pressed2);
                SharedData.filterClickRecord[SharedData.SHORT_TIME] = true;
            } else {
                v.setBackgroundResource(R.drawable.filter_button_unpressed);
                SharedData.filterClickRecord[SharedData.SHORT_TIME] = false;
            }
            activateFilter();
        });
        filterByDifficult.setOnClickListener(v -> {
            if (!SharedData.filterClickRecord[SharedData.EASY_TO_MAKE]) {
                v.setBackgroundResource(R.drawable.filter_button_pressed2);
                SharedData.filterClickRecord[SharedData.EASY_TO_MAKE] = true;
            } else {
                v.setBackgroundResource(R.drawable.filter_button_unpressed);
                SharedData.filterClickRecord[SharedData.EASY_TO_MAKE] = false;
            }
            activateFilter();
        });
    }

    private void activateFilter() {
        if (currFilter == Filters.INGREDIENTS) {
            searchAdapter = new SearchAdapter(searchRecipes, SharedData.INGREDIENTS_FILTER);
        } else if (currFilter == Filters.ALL) {
            searchAdapter = new SearchAdapter(searchRecipes, SharedData.NAME_FILTER);
        }
        searchAdapter.getFilter().filter(searchText);
        refreshAdapters();
    }

    private void handelByIngredientsFilter() {
        filterByIngredients.setOnClickListener(v -> {
            if (currFilter != Filters.INGREDIENTS) {
                v.setBackgroundResource(R.drawable.filter_button_pressed);
                currFilter = Filters.INGREDIENTS;
                setFilterPressed(filterByIngredients);
                activateFilter();
            }
        });
    }

    private void refreshAdapters() {
        setUpAdapterListeners();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }

    private void handelAllFilter() {
        filterAll.setOnClickListener(v -> {
            if (currFilter != Filters.ALL) {
                v.setBackgroundResource(R.drawable.filter_button_pressed);
                currFilter = Filters.ALL;
                setFilterPressed(filterAll);
                activateFilter();
            }
        });
    }

    private void setFilterPressed(Button currentFilterPressed) {
        for (Button filter : filterButtons) {
            if (filter != currentFilterPressed)
                filter.setBackgroundResource(R.drawable.filter_button_unpressed);
        }
    }

    private void configureFilterButtons() {
        filterAll = findViewById(R.id.filterByAll);
        filterByIngredients = findViewById(R.id.filterByIngredients);
        filterBycalories = findViewById(R.id.filterLowCalories);
        filterByProtein = findViewById(R.id.filterHighProtein);
        filterByTime = findViewById(R.id.filterByTime);
        filterByDifficult = findViewById(R.id.filterByDifficult);

        filterButtons = new ArrayList<>
                (Arrays.asList(filterAll, filterByIngredients));
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
                searchText = newText;
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

    private void setUpAdapterListeners() {
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

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        searchAdapter = new SearchAdapter(searchRecipes, SharedData.NAME_FILTER);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }

}
