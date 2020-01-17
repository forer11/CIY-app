package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        // define the bottom navigation bar to be used in the activity
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable logo = ContextCompat.getDrawable(this, R.drawable.toolbar_logo);
        Bitmap bitmap = ((BitmapDrawable) logo).getBitmap();
        // Scale it to 50 x 50
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));
        // Set your new, scaled drawable "d"
        toolbar.setLogo(d);

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
        getMenuInflater().inflate(R.menu.tool_bar_buttons,menu);

        //get the image of the user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Uri uri = currentUser.getPhotoUrl();
            if ( uri == null ) // user not sign in from google, so default profile picture defined
            {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.profile_default));
            }
            else
            {
                try
                {
                Picasso.get()
                .load(uri)
                .fit()
                .centerCrop()
                .into((ImageView)findViewById(R.id.profile_photo));
                menu.getItem(0).setIcon(((ImageView) findViewById(R.id.profile_photo)).getDrawable());
                }
                catch (Exception e)
                {
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.profile_default));
                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_status:

                int res_id = item.getItemId();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(BottomNavigationBar.this);

                mBuilder.setTitle("Hi you");
                mBuilder.setMessage("Wer'e sorry to see you go");
                mBuilder.setCancelable(false)
                        .setPositiveButton("sign out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(BottomNavigationBar.this, "logOut", Toast.LENGTH_SHORT).show(); //TODO SHANI
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                firebaseAuth.signOut();
                                Intent intent = new Intent(getBaseContext(), loginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                final AlertDialog alertdialog = mBuilder.create();
                alertdialog.show();

                alertdialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

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
