package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This activity represents the BottomNavigationBar of the app. It creates 3 fragments:
 * HomeFragment, FavoritesFragment, and SearchFragment, and activate the corresponding fragment the
 * user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBar extends AppCompatActivity {

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            favoritesFragment = new FavoritesFragment();
            searchFragment = new SearchFragment();
        }
        //setting home fragment as default
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homeFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tool_bar_buttons, menu);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.shani));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_status: //TODO SHANI DIALOG
                Toast.makeText(this, "logOut", Toast.LENGTH_SHORT).show();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = new Intent(getBaseContext(), loginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    displayHomeFragment();
                    break;
                case R.id.nav_favorites:
                    displayFavoritesFragment();
                    break;
                case R.id.nav_search:
                    displaySearchFragment();
                    break;
            }
            return true;
        }
    };

    protected void displayHomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(homeFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, homeFragment);
        }
        // Hide Favorites fragment
        if (favoritesFragment.isAdded()) {
            fragmentTransaction.hide(favoritesFragment);
        }
        // Hide Search fragment
        if (searchFragment.isAdded()) {
            fragmentTransaction.hide(searchFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displayFavoritesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (favoritesFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(favoritesFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, favoritesFragment);
        }
        // Hide Favorites fragment
        if (homeFragment.isAdded()) {
            fragmentTransaction.hide(homeFragment);
        }
        // Hide Search fragment
        if (searchFragment.isAdded()) {
            fragmentTransaction.hide(searchFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displaySearchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (searchFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(searchFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, searchFragment);
        }
        // Hide Favorites fragment
        if (homeFragment.isAdded()) {
            fragmentTransaction.hide(homeFragment);
        }
        // Hide Search fragment
        if (favoritesFragment.isAdded()) {
            fragmentTransaction.hide(favoritesFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }
}
