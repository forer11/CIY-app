package com.example.ciy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * the activity from which we search recipes and filter the search results
 */
public class SearchRecipeActivity extends AppCompatActivity {

    private static final String DATA_FAILURE_MSG = "Failed to load data";
    private static final String FROM_SEARCH_TAG = "RecipeFromSearchRecipe";

    private enum Filters {
        ALL, INGREDIENTS
    }

    /* the search RecyclerView */
    private RecyclerView recyclerView;
    /* the layout manager for the RecyclerView */
    private RecyclerView.LayoutManager layoutManager;
    /* the list of recipes used for the search activity */
    private ArrayList<Recipe> searchRecipes;
    /* the search recyclerView adapter */
    private SearchAdapter searchAdapter;

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /* the current user registered to our database */
    private FirebaseUser user;
    /* the button responsible for filtering by ingredients or by alphabetical order (normal) */
    private Button filterAll, filterByIngredients;
    /* buttons responsible for the categorical threshold filtering */
    private Button filterByCalories, filterByProtein, filterByTime, filterByDifficult;
    /* array list packaging thr filter buttons */
    private ArrayList<Button> filterButtons;
    /* indicates which main filter is currently on, the categorical ones can be activated together*/
    private Filters currFilter;
    /* the text the user currently search for */
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

        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);
        setUpRecyclerView();
        setUpAdapterListeners();

    }

    /**
     * set all the filters actions.
     */
    private void setupFilterListener() {
        handelAllFilter();
        handelByIngredientsFilter();

        setCategoricalFilter(filterByCalories, SharedData.LOW_CALORIES);
        setCategoricalFilter(filterByProtein, SharedData.HIGH_PROTEIN);
        setCategoricalFilter(filterByTime, SharedData.SHORT_TIME);
        setCategoricalFilter(filterByDifficult, SharedData.EASY_TO_MAKE);
    }

    /**
     * sets the behavior for the categorical filters.
     *
     * @param categoryFilter the filter we set
     * @param filterIndex    the index of the filter on the filterRecord
     */
    private void setCategoricalFilter(Button categoryFilter, int filterIndex) {
        categoryFilter.setOnClickListener(v -> {
            if (!SharedData.filterClickRecord[filterIndex]) {
                v.setBackgroundResource(R.drawable.filter_button_pressed2);
                SharedData.filterClickRecord[filterIndex] = true;
            } else {
                v.setBackgroundResource(R.drawable.filter_button_unpressed);
                SharedData.filterClickRecord[filterIndex] = false;

            }
            activateFilter();
        });
    }

    /**
     * activates the main filter, i.e by ingredients or by alphabetical order
     */
    private void activateFilter() {
        if (currFilter == Filters.INGREDIENTS) {
            searchAdapter = new SearchAdapter(searchRecipes, SharedData.INGREDIENTS_FILTER);
        } else if (currFilter == Filters.ALL) {
            searchAdapter = new SearchAdapter(searchRecipes, SharedData.NAME_FILTER);
        }
        searchAdapter.getFilter().filter(searchText);
        refreshAdapters();
    }

    /**
     * define the action happening when pressing the by ingredients filter
     */
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

    /**
     * define the action happening when pressing the by all filter
     */
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

    /**
     * refresh the adapter and recyclerView when we switch filters
     */
    private void refreshAdapters() {
        setUpAdapterListeners();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }

    /**
     * removes the color from the other filter not currently pressed
     *
     * @param currentFilterPressed the filter currently active
     */
    private void setFilterPressed(Button currentFilterPressed) {
        for (Button filter : filterButtons) {
            if (filter != currentFilterPressed)
                filter.setBackgroundResource(R.drawable.filter_button_unpressed);
        }
    }

    /**
     * configures the filter button objects
     */
    private void configureFilterButtons() {
        filterAll = findViewById(R.id.filterByAll);
        filterByIngredients = findViewById(R.id.filterByIngredients);
        filterByCalories = findViewById(R.id.filterLowCalories);
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
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setIconified(false);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setIconifiedByDefault(false);
        setUpSearchViewListeners(searchItem, searchView);
        return true;
    }

    /**
     * sets the search view actions, where we type the search in hope for results
     *
     * @param searchItem the search item in the toolbar menu
     * @param searchView the search view object
     */
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

    /**
     * sets the listeners for the recycleView
     */
    private void setUpAdapterListeners() {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                setUpRecipeFragment(searchRecipes.get(position));
            }

            @Override
            public void OnLikeClick(int position) {
                searchRecipes.remove(position);
                searchAdapter.notifyItemRemoved(position);
            }
        });
    }

    /**
     * sets the data needed to open the recipe page
     *
     * @param recipe the clicked recipe
     */
    private void setUpRecipeFragment(final Recipe recipe) {
        // reference to the firestore favorites collection
        CollectionReference favoritesRef = usersRef.
                document(user.getUid()).collection(SharedData.Favorites);
        // search for the recipe in the user favorites, if exists set the like parameter
        // to be true, other wise false, then send the data to the recipe page.
        final DocumentReference favoriteRecipeRef = favoritesRef.document(recipe.getId());
        favoriteRecipeRef.get().addOnSuccessListener(documentSnapshot -> {
            boolean userPressedLike = false;
            if (!(documentSnapshot == null || !documentSnapshot.exists())) {
                userPressedLike = true;
            }
            showRecipeFragment(recipe, userPressedLike);
        }).addOnFailureListener(e -> Toast.makeText(SearchRecipeActivity.this,
                DATA_FAILURE_MSG, Toast.LENGTH_SHORT).show());
    }

    /**
     * shows the recipe page of the recipe the user clicked on
     *
     * @param recipe          the clicked recipe
     * @param userPressedLike true if in the user's favorites, false otherwise
     */
    private void showRecipeFragment(Recipe recipe, boolean userPressedLike) {
        RecipeFragment recipeFragment = RecipeFragment.newInstance(recipe, userPressedLike
                , SharedData.SEARCH_RECIPE);
        FragmentManager fragmentManager = Objects.requireNonNull(SearchRecipeActivity.this)
                .getSupportFragmentManager();
        recipeFragment.show(fragmentManager, FROM_SEARCH_TAG);
    }

    /**
     * set the recyclerView object and the adapter
     */
    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        searchAdapter = new SearchAdapter(searchRecipes, SharedData.NAME_FILTER);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }

}
