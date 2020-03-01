package com.example.ciy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Objects;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

/**
 * This activity represents the BottomNavigationBar of the app. It creates 3 fragments:
 * DiscoverFragment, FavoritesFragment, and SearchFragment, and activate the corresponding fragment the
 * user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBar extends AppCompatActivity {
    /* the Home fragment Tag */
    private static final String HOME = "Home";
    /* the Favorites fragment Tag */
    private static final String FAVORITES = "Favorites";
    /* the Search fragment Tag */
    private static final String SEARCH = "Search";
    /* the Discover fragment Tag */
    private static final String DISCOVER = "Discover";
    private static final int ADD_RECIPE_REQUEST_CODE = 2;
    private static final int SEARCH_RECIPE_REQUEST_CODE = 22;
    private static final int ERROR = -1;
    /* the Home fragment */
    HomeFragment homeFragment;
    /* the Discover fragment */
    DiscoverFragment discoverFragment;
    /* the Favorites fragment */
    FavoritesFragment favoritesFragment;
    /* the Search fragment */
    SearchFragment searchFragment;
    /* the indicator of the last fragment we showed/added */
    int lastPushed = SharedData.DEFAULT;
    /* the tag of the last fragment we showed/added */
    private String lastTag = null;

    /* app's Bottom navigation bar */
    private BottomNavigationView bottomNav;

    private SharedPreferences prefs = null;

    /* the FireBase authenticator */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private Uri uri = null;

    RoundedBitmapDrawable rounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            uri = currentUser.getPhotoUrl();
        }
        // set bottom and top bars
        setBars();

        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            discoverFragment = new DiscoverFragment();
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
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        fromSignIn();
    }


    /**
     * this function checks if the user came from the loginActivity and if so, display the app
     */
    private void fromSignIn(){
        String flag = getIntent().getStringExtra("I_CAME_FROM");
        if (flag != null){
            if(flag.equals("Login")){
                showIntro("Home", "Your Fridge",
                        bottomNav.getMenu().findItem(R.id.navHome).getItemId(), 1);
            }
        }
    }

//    /**
//     * this function detect if it's a first time user and displayed the intro in such case
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        // checks if the user entered the app for the first time
//        if (prefs.getBoolean("firstrun", true)) {
//            showIntro("Home", "Your Fridge",
//                    bottomNav.getMenu().findItem(R.id.navHome).getItemId(), 1);
//            prefs.edit().putBoolean("firstrun", false).apply();
//        }
//    }


    /**
     * this function responsible on introduce the app for first time users
     */
    private void showIntro(String title, String text, int viewId, final int type) {

        final int navDiscover = bottomNav.getMenu().findItem(R.id.navDiscover).getItemId();
        final int navFavorites = bottomNav.getMenu().findItem(R.id.navFavorites).getItemId();
        final int navAddRecipe = bottomNav.getMenu().findItem(R.id.navAddRecipe).getItemId();

        new GuideView.Builder(this)
                .setTitle(title)
                .setContentText(text)
                .setTargetView(findViewById(viewId))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setDismissType(GuideView.DismissType.anywhere) //optional - default dismissible by TargetView
                .setGuideListener(new GuideView.GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (type) {
                            case 1:
                                showIntro("Discover", "Discover new recipes", navDiscover, 2);
                                break;
                            case 2:
                                showIntro("Favorites", "Your favorites recipes", navFavorites, 3);
                                break;
                            case 3:
                                showIntro("Add new recipe", "Add your own recipe", navAddRecipe, 4);
                                break;
                            case 4:
                                showIntro("Search to add more ingredients", "Add other ingredients", R.id.enterIngredients, 5);
                                break;
                            case 5:
                                showIntro("Basic Ingredients", "Drag some basic ingredients to your Basic Ingredients Shelf", R.id.dragIngredients, 6);
                                break;
                            case 6:
                                showIntro("Basic Ingredients Shelf", "Drag your basic ingredients here", R.id.basicIngredientsShelf, 7);
                                break;
                            case 7:
                                showIntro("Your Fridge", "Tap to open your fridge\nSwipe right or left to remove ingredients", R.id.fridge_button, 8);
                                break;
                            case 8:
                                showIntro("Search", "Search", R.id.actionSearchNavigation, 9);
                                break;
                        }
                    }
                })
                .build()
                .show();
    }


    /**
     * sets the bottom navigation bar and upper toolbar
     */
    private void setBars() {
        // define the bottom navigation bar to be used in the activity
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // define the toolbar to be used in the activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable logo = ContextCompat.getDrawable(this, R.drawable.women_logo);
        Bitmap bitmap = ((BitmapDrawable) logo).getBitmap();
        // Scale it to 50 x 50
//        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));
        // Set your new, scaled drawable "d"
        toolbar.setLogo(logo);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_buttons, menu);

        //get the image of the user
        menu.findItem(R.id.icon_status).setIcon(ContextCompat.getDrawable(this, R.drawable.profile_default));

        if (uri != null) // user not sign in from google, so default profile picture defined
        {

            setProfileImage(menu, uri);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_status:
                return showSignOutDialog();
            case R.id.actionSearchNavigation:
                Intent intent = new Intent(getBaseContext(), SearchRecipeActivity.class);
                startActivityForResult(intent, SEARCH_RECIPE_REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navHome: //TODO LIOR, IF PRESSED INSIDE HOME, SCROLL TO TOP
                    homePressHandler();
                    break;
                case R.id.navDiscover:
                    discoverPressHandler();
                    break;
                case R.id.navFavorites:
                    showFragment(favoritesFragment, FAVORITES, lastTag);
                    lastPushed = SharedData.FAVORITES;
                    lastTag = FAVORITES;
                    break;
//                case R.id.navSearch:
//                    showFragment(searchFragment, SEARCH, lastTag);
//                    lastPushed = SharedData.SEARCH;
//                    lastTag = SEARCH;
//                    break;
                case R.id.navAddRecipe:
                    startActivityForResult(new Intent(BottomNavigationBar.this,
                            NewRecipeActivity.class), ADD_RECIPE_REQUEST_CODE);

                    break;
            }
            return true;
        }
    };

    /**
     * handling the event we pressed the home icon in the bottom navigation bar
     */
    private void homePressHandler() {

        showFragment(homeFragment, HOME, lastTag);
        lastPushed = SharedData.HOME;
        lastTag = HOME;
    }

    private void discoverPressHandler() {
        if (lastPushed == SharedData.DISCOVER) {
            discoverFragment.scrollToTop();
        } else {
            showFragment(discoverFragment, DISCOVER, lastTag);
            lastPushed = SharedData.DISCOVER;
            lastTag = DISCOVER;
        }
    }

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

    /***
     * show the sign out dialog on screen
     */
    private boolean showSignOutDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BottomNavigationBar.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_profile_preseed, null);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            ImageView profile = (ImageView) view.findViewById(R.id.profile_image);
            if (uri != null) {
                try {
                    Picasso.get().load(uri).into(profile);
                } catch (Exception e) {
                }
            }
            String username = currentUser.getDisplayName();
            if (username != null && !username.equals("")) {
                TextView user_info = (TextView) view.findViewById(R.id.user_details);
                user_info.setText(username + "\n" + currentUser.getEmail());
            }
        }

        mBuilder.setView(view);

        final AlertDialog alertdialog = mBuilder.create();
        Button signout = (Button) view.findViewById(R.id.signout_button);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button signin = (Button) view.findViewById(R.id.signin_button);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button addingredients = (Button) view.findViewById(R.id.what_in_my_fridge);
        addingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertdialog.cancel();
                FragmentManager fragmentManager = Objects.requireNonNull(BottomNavigationBar.this)
                        .getSupportFragmentManager();
                SearchFragment searchFragment = ((BottomNavigationBar) BottomNavigationBar.this).searchFragment;
                searchFragment.show(fragmentManager, "FridgeFromHome");

            }
        });
        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();

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
                rounded = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                rounded.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()));
                rounded.setBounds(0, 0, 5, 5);
                menu.findItem(R.id.icon_status).setIcon(rounded);
//                menu.getItem(0).setIcon(rounded);
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
        } else {
            homePressHandler();
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.getMenu().findItem(R.id.navHome).setChecked(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == ADD_RECIPE_REQUEST_CODE) {
            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

            int navIcon = returnNavIcon();
            if (navIcon != ERROR) {
                bottomNavigationView.getMenu().findItem(navIcon).setChecked(true);
            }
        }
        if (requestCode == SEARCH_RECIPE_REQUEST_CODE) {
            SharedData.filterClickRecord = new boolean[]{false, false, false, false};
        }
    }

    private int returnNavIcon() {
        switch (lastPushed) {
            case SharedData.HOME:
                return R.id.navHome;
            case SharedData.DISCOVER:
                return R.id.navDiscover;
            case SharedData.FAVORITES:
                return R.id.navFavorites;
//            case SharedData.SEARCH:
//                return R.id.navSearch;
            default:
                return ERROR;
        }
    }

}
