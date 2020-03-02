package com.example.ciy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.Objects;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;

/**
 * This activity represents the BottomNavigationBar of the app. It creates 3 fragments:
 * DiscoverFragment, FavoritesFragment, and FridgeFragment, and activate the corresponding
 * fragment the user requested- by typing at the corresponding icon in the bottom navigation bar.
 */
public class BottomNavigationBar extends AppCompatActivity {

    //constants

    /* the Home fragment Tag */
    private static final String HOME = "Home";
    /* the Favorites fragment Tag */
    private static final String FAVORITES = "Favorites";
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
    FridgeFragment fridgeFragment;
    /* the indicator of the last fragment we showed/added */
    int lastPushed = SharedData.DEFAULT;
    /* the tag of the last fragment we showed/added */
    private String lastTag = null;

    /* the constants of the elements that displayed in the intro */
    private static final String HOME_EXPLANATION = "Your home screen where you can change and " +
            "add ingredients";
    private static final String DISCOVER_EXPLANATION = "Discover new recipes";
    private static final String FAVORITES_EXPLANATION = "Check your favorites recipes";
    private static final String ADD_RECIPE_EXPLANATION = "Add your own recipe";
    private static final String SEARCH_RECIPES_EXPLANATION = "Here you search recipes by name, you can " +
            "find which recipes ingredients match the ones you currently have and filter the " +
            "search with multiple filter tags.";
    private static final String SEARCH_INGREDIENTS_EXPLANATION = "Search and add more ingredients here";
    private static final String BASIC_INGREDIENTS_EXPLANATION = "Here are all the must have " +
            "basic ingredients you probably have in your kitchen";
    private static final String SHELF_EXPLANATION = "Drag your basic ingredients here and add " +
            "them to your fridge";
    private static final String FRIDGE_EXPLANATION = "Tap to open your fridge Swipe right or " +
            "left to remove ingredients";
    private static final int INTRO_TEXT_SIZE = 16;
    private static final String LOGIN_ACTIVITY_FLAG_VALUE = "LoginActivity";
    private static final String LOGIN_ACTIVITY_FLAG_KEY = "I_CAME_FROM";
    private static final int DISCOVER_INTRO_IDX = 1;
    private static final int FAVORITES_INTRO_IDX = 2;
    private static final int ADD_RECIPE_INTRO_IDX = 3;
    private static final int SEARCH_RECIPES_INTRO_IDX = 4;
    private static final int SEARCH_INGREDIENTS_INTRO_IDX = 5;
    private static final int BASIC_INGREDIENTS_INTRO_IDX = 6;
    private static final int SHELF_INTRO_IDX = 7;
    private static final int FRIDGE_INTRO_IDX = 8;



    /* app's Bottom navigation bar */
    private BottomNavigationView bottomNav;

    /* the FireBase authenticator */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private Uri uri = null; //TODO shani set better name and document

    RoundedBitmapDrawable rounded; //TODO shani set better name and document

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
            fridgeFragment = new FridgeFragment();

        }
        //setting home fragment as default
        setsDefaultFragment();
        appTutorial();
    }

    /**
     * sets the default fragment to be the home fragment
     */
    private void setsDefaultFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .add(R.id.fragment_container, homeFragment, HOME)
                .setBreadCrumbShortTitle(HOME);
        transaction.commit();
        lastPushed = SharedData.HOME;
        lastTag = HOME;
    }


    /**
     * this function checks if the user launch from the loginActivity and if so,
     * display the app tutorial
     */
    private void appTutorial() {
        String flag = getIntent().getStringExtra(LOGIN_ACTIVITY_FLAG_KEY);
        if (flag != null) {
            if (flag.equals(LOGIN_ACTIVITY_FLAG_VALUE)) {
                displayAppTutorial(HOME_EXPLANATION,
                        bottomNav.getMenu().findItem(R.id.navHome).getItemId(), DISCOVER_INTRO_IDX);
            }
        }
    }


    /**
     * this function responsible on introduce the app for first time users
     * @param title - the title of the current element to display
     * @param viewId - the current element id
     * @param elementId - indicates on the next element to display
     */
    private void displayAppTutorial(String title, int viewId, final int elementId) {

        final int navDiscover = bottomNav.getMenu().findItem(R.id.navDiscover).getItemId();
        final int navFavorites = bottomNav.getMenu().findItem(R.id.navFavorites).getItemId();
        final int navAddRecipe = bottomNav.getMenu().findItem(R.id.navAddRecipe).getItemId();

        new GuideView.Builder(this)
                .setTitle(title)
                .setTargetView(findViewById(viewId))
                .setTitleTextSize(INTRO_TEXT_SIZE)
                .setGravity(GuideView.Gravity.center)
                .setDismissType(GuideView.DismissType.anywhere)
                .setGuideListener(view -> {
                    switch (elementId) {
                        case DISCOVER_INTRO_IDX:
                            displayAppTutorial(DISCOVER_EXPLANATION, navDiscover, FAVORITES_INTRO_IDX);
                            break;
                        case FAVORITES_INTRO_IDX:
                            displayAppTutorial(FAVORITES_EXPLANATION, navFavorites, ADD_RECIPE_INTRO_IDX);
                            break;
                        case ADD_RECIPE_INTRO_IDX:
                            displayAppTutorial(ADD_RECIPE_EXPLANATION, navAddRecipe, SEARCH_RECIPES_INTRO_IDX);
                            break;
                        case SEARCH_RECIPES_INTRO_IDX:
                            displayAppTutorial(SEARCH_RECIPES_EXPLANATION, R.id.actionSearchNavigation, SEARCH_INGREDIENTS_INTRO_IDX);
                            break;
                        case SEARCH_INGREDIENTS_INTRO_IDX:
                            displayAppTutorial(SEARCH_INGREDIENTS_EXPLANATION, R.id.enterIngredients, BASIC_INGREDIENTS_INTRO_IDX);
                            break;
                        case BASIC_INGREDIENTS_INTRO_IDX:
                            displayAppTutorial(BASIC_INGREDIENTS_EXPLANATION, R.id.dragIngredients, SHELF_INTRO_IDX);
                            break;
                        case SHELF_INTRO_IDX:
                            displayAppTutorial(SHELF_EXPLANATION, R.id.basicIngredientsShelf, FRIDGE_INTRO_IDX);
                            break;
                        case FRIDGE_INTRO_IDX:
                            displayAppTutorial(FRIDGE_EXPLANATION, R.id.fridge_button, 9);
                            break;
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
        toolbar.setLogo(logo);
    }


    /**
     * create to design of the toolbar //TODO - shani or hagai - unclear
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_buttons, menu);

        // user not sign in, so default profile picture defined
        menu.findItem(R.id.icon_status).setIcon(ContextCompat.getDrawable(this, R.drawable.profile_default));

        if (uri != null) // user got profile photo, set it as icon
        {
            setProfileImage(menu, uri);
        }
        return true;
    }


    /**
     * act in response to user selection
     */
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

    /**
     * moves to the corresponding fragment according to user's choise
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.navHome:
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
                        case R.id.navAddRecipe:
                            startActivityForResult(new Intent(BottomNavigationBar.this,
                                    AddRecipeActivity.class), ADD_RECIPE_REQUEST_CODE);
                            break;
                    }
                    return true;
                }
            };

    /**
     * handles the event were we pressed at the home icon in the bottom navigation bar
     */
    private void homePressHandler() {

        showFragment(homeFragment, HOME, lastTag);
        lastPushed = SharedData.HOME;
        lastTag = HOME;
    }

    /**
     * handles the event were we pressed at the discover icon in the bottom navigation bar
     */
    private void discoverPressHandler() {
        if (lastPushed == SharedData.DISCOVER) {
            discoverFragment.scrollToTop();
        } else {
            showFragment(discoverFragment, DISCOVER, lastTag);
            lastPushed = SharedData.DISCOVER;
            lastTag = DISCOVER;
        }
    }

    /**
     * This method shows the wanted fragment and hides the previous one
     *
     * @param fragment the wanted fragment that we want to show
     * @param tag      the tag of the wanted fragment
     * @param lastTag  the tag of the previous fragment that we want to hide
     */
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

    /**
     * set the layout of the dialog //TODO - hagai or shani - documentation of args is missing
     */
    private void setDialogView(AlertDialog.Builder mBuilder, View view) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            ImageView profile = view.findViewById(R.id.profile_image);
            if (uri != null) {
                try {
                    Picasso.get().load(uri).into(profile);
                } catch (Exception e) {
                }
            }
            String username = currentUser.getDisplayName();
            if (username != null) {
                TextView user_info = view.findViewById(R.id.user_details);
                if (!username.equals("")) {
                    user_info.setText(username + "\n" + currentUser.getEmail());
                } else {
                    String number = currentUser.getPhoneNumber();
                    user_info.setText(number);
                }
            }
        }
        mBuilder.setView(view);
    }

    /**
     * set the actions on every user selection on the dialog //TODO - hagai or shani - documentation of args is missing
     */
    private void onClickDialog(View view, AlertDialog alertDialog) {
        Button signOut = view.findViewById(R.id.signout_button);
        signOut.setOnClickListener(view1 -> {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button addingredients = view.findViewById(R.id.what_in_my_fridge);
        addingredients.setOnClickListener(view12 -> {
            alertDialog.cancel();
            FragmentManager fragmentManager = Objects.requireNonNull
                    (BottomNavigationBar.this).getSupportFragmentManager();
            FridgeFragment fridgeFragment = (BottomNavigationBar.this).fridgeFragment;
            fridgeFragment.show(fragmentManager, "FridgeFromHome");
        });
    }

    /**
     * show the sign out dialog on screen
     */
    private boolean showSignOutDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BottomNavigationBar.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_user_status, null);
        setDialogView(mBuilder, view);
        final AlertDialog alertdialog = mBuilder.create();
        onClickDialog(view, alertdialog);
        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertdialog.show();

        return true;
    }

    /**
     * get menu resource and url for user profile photo, and shows the image on the menu
     *
     * @param menu the menu bar object of the app or activity
     * @param uri  the url for the user's profile photo
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


    /**
     * handles the case when the user presses the back button
     */
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

    /**
     * mark the icon of the current fragment were at in the bottom navigation bar
     *
     * @return the id of that icon, error otherwise
     */
    private int returnNavIcon() {
        switch (lastPushed) {
            case SharedData.HOME:
                return R.id.navHome;
            case SharedData.DISCOVER:
                return R.id.navDiscover;
            case SharedData.FAVORITES:
                return R.id.navFavorites;
            default:
                return ERROR;
        }
    }

}
