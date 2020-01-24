package com.example.ciy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * This activity represents the BottomNavigationBar of the app. It creates 3 fragments:
 * HomeFragment, FavoritesFragment, and SearchFragment, and activate the corresponding fragment the
 * user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBar extends AppCompatActivity {
    /* the Home fragment Tag */
    private static final String HOME = "Home";
    /* the Favorites fragment Tag */
    private static final String FAVORITES = "Favorites";
    /* the Search fragment Tag */
    private static final String SEARCH = "Search";
    /* the Home fragment */
    HomeFragment homeFragment;
    /* the Favorites fragment */
    FavoritesFragment favoritesFragment;
    /* the Search fragment */
    SearchFragment searchFragment;
    /* the indicator of the last fragment we showed/added */
    int lastPushed = SharedData.DEFAULT;
    /* the tag of the last fragment we showed/added */
    private String lastTag = null;


    /* the FireBase authenticator */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);
        // set bottom and top bars
        setBars();

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
        lastPushed = SharedData.HOME;
        lastTag = HOME;

    }

    /**
     * sets the bottom navigation bar and upper toolbar
     */
    private void setBars() {
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
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_buttons, menu);

        //get the image of the user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Uri uri = currentUser.getPhotoUrl();
            if (uri == null) // user not sign in from google, so default profile picture defined
            {
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.profile_default));
            } else {
                setProfileImage(menu, uri);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_status:
                return showSignOutDialog();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home: //TODO LIOR, IF PRESSED INSIDE HOME, SCROLL TO TOP
                    homePressHandler();
                    break;
                case R.id.nav_favorites:
                    showFragment(favoritesFragment, FAVORITES, lastTag);
                    lastPushed = SharedData.FAVORITES;
                    lastTag = FAVORITES;
                    break;
                case R.id.nav_search:
                    showFragment(searchFragment, SEARCH, lastTag);
                    lastPushed = SharedData.SEARCH;
                    lastTag = SEARCH;
                    break;
            }
            return true;
        }
    };

    /**
     * handling the event we pressed the home icon in the bottom navigation bar
     */
    private void homePressHandler() {
        if (lastPushed == SharedData.HOME) {
            if (homeFragment.isRecipeCurrentlyOpen()) {
                getSupportFragmentManager().popBackStack();
            } else {
                homeFragment.scrollToTop();
            }
        } else {
            showFragment(homeFragment, HOME, lastTag);
            lastPushed = SharedData.HOME;
            lastTag = HOME;
        }
    }

    private void showFragment(Fragment fragment, String tag, String lastTag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (lastTag != null) {
            Fragment lastFragment = fragmentManager.findFragmentByTag(lastTag);
            if (lastTag.equals(FAVORITES)&&favoritesFragment.isRecipeCurrentlyOpen())
            {
                getSupportFragmentManager().popBackStack();
            }
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

    /***
     * show the sign out dialog on screen
     */
    private boolean showSignOutDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BottomNavigationBar.this);

        mBuilder.setTitle("Hi you");
        mBuilder.setMessage("Wer'e sorry to see you go");
        mBuilder.setCancelable(true)
                .setPositiveButton("sign out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(BottomNavigationBar.this, "logOut", Toast.LENGTH_SHORT).show(); //TODO SHANI
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
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
    }

    /***
     * get menu resource and url for user profile photo, and shows the image on the menu
     * @param menu the menu bar object of the app or activity
     * @param uri the url for the user's profile photo
     */
    private void setProfileImage(final Menu menu, Uri uri) {
        //create a new target to be used with picasso
        final Target mTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                Log.d("DEBUG", "onBitmapLoaded");
                RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                rounded.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()));
                rounded.setBounds(0, 0, 5, 5);
                menu.getItem(0).setIcon(rounded);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable drawable) {
                Log.d("DEBUG", "onBitmapFailed");
            }

            @Override
            public void onPrepareLoad(Drawable drawable) {
                Log.d("DEBUG", "onPrepareLoad");
            }
        };
        // set the image to be presented on the menu bar
        Picasso.get().load(uri).into(mTarget);
    }

    @Override
    public void onBackPressed() {
        if (lastPushed == SharedData.HOME) {
            super.onBackPressed();
        }
        // currently does the same as above, need to see if we want different behavior TODO Lior
        else if (lastPushed == SharedData.FAVORITES && favoritesFragment.isRecipeCurrentlyOpen()) {
            super.onBackPressed();
        } else {
            homePressHandler();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        }
    }
}
