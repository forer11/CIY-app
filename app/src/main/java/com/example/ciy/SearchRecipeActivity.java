package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

public class SearchRecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_recipe);
        final ArrayList<Recipe> searchRecipes = new ArrayList<>(SharedData.searchRecipes);

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView(searchRecipes);
        setUpListeners(searchRecipes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        SearchView searchView =  (SearchView) searchItem.getActionView();

        // will not enable search click, only realtime search //TODO Lior
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

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
                Intent intent = new Intent(getBaseContext(), BottomNavigationBar.class);
                startActivity(intent);
                finish();
                return true;
            }
        });
        return true;
    }

    private void setUpListeners(final ArrayList<Recipe> searchRecipes) {
        searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                searchRecipes.get(position).setId("wichongo");
                searchAdapter.notifyItemChanged(position);
            }

            @Override
            public void OnLikeClick(int position) {
                searchRecipes.remove(position);
                searchAdapter.notifyItemRemoved(position);
            }
        });
    }

    private void setUpRecyclerView(ArrayList<Recipe> searchRecipes) {
        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        searchAdapter = new SearchAdapter(searchRecipes);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
    }
}
