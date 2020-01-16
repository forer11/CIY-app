package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * This activity represents the BottomNavigationBar of the app. It creates 3 fragments:
 * HomeFragment, FavoritesFragment, and SearchFragment, and activate the corresponding fragment the
 * user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBar extends AppCompatActivity {

    private static final String HOME = "Home";
    private static final String FAVORITES = "Favorites";
    private static final String SEARCH = "Search";
    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private SearchFragment searchFragment;
    private String lastPushed = null;


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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .add(R.id.fragment_container, homeFragment, HOME)
                .setBreadCrumbShortTitle(HOME);
        transaction.commit();
        lastPushed = HOME;

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
            case R.id.icon_status:
                Toast.makeText(this, "logOut", Toast.LENGTH_SHORT).show(); //TODO SHANI
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
                    showFragment(homeFragment, HOME, lastPushed);
                    lastPushed = HOME;
                    break;
                case R.id.nav_favorites:
                    showFragment(favoritesFragment, FAVORITES, lastPushed);
                    lastPushed = FAVORITES;
                    break;
                case R.id.nav_search:
                    showFragment(searchFragment, SEARCH, lastPushed);
                    lastPushed = SEARCH;
                    break;
            }
            return true;
        }
    };

    private void showFragment(Fragment fragment, String tag, String lastTag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastFragment != null) {
                transaction.hide(lastFragment);
            }
        }

        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment, tag).setBreadCrumbShortTitle(tag);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (lastPushed.equals(HOME)) {
            super.onBackPressed();
        }
        showFragment(homeFragment, HOME, lastPushed);
        lastPushed = HOME;
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }
}
