package com.example.ciy;


import com.airbnb.lottie.LottieAnimationView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * This Dialog Fragment represents a specific recipe and contains its ingredients, its image,
 * its instruction and some information about it like how many views of other users it has etc.
 */
public class RecipeFragment extends DialogFragment {
    /* the recipe we show in this page */
    private Recipe recipe;
    /* the lottie animation like button */
    private LottieAnimationView button_like;
    /* indicates if the user pressed like, we get the data
    when we open this fragment from the server */
    private boolean userPressedLike;
    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* reference to the individual user favorites collection */
    private CollectionReference favoritesRef;
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /* indicates from which activity we opened the recipe*/
    private int sourceActivity;

    //recipe details
    private TextView recipeTitle;
    private ImageView recipeImage;
    private TextView ingredientsTitle;
    private TextView titleRecipeDescription;
    private LinearLayout recipeDescription;

    //recipe metadata
    private TextView likes_and_views;
    private TextView protein;
    private TextView prepareTime;
    private TextView complexity;
    private TextView calories;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // sets the dialog to be full screen
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.BaseAppTheme);
        // Get back arguments
        recipe = (Recipe) getArguments().getSerializable("recipe");
        userPressedLike = getArguments().getBoolean("userPressedLike");
        sourceActivity = getArguments().getInt("activity");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        favoritesRef = usersRef.document(user.getUid()).collection(SharedData.Favorites);

    }

    private void initRecipeData() {
        //recipe details
        recipeTitle = getView().findViewById(R.id.recipeTitle);
        recipeImage = getView().findViewById(R.id.recipeImage);
        ingredientsTitle = getView().findViewById(R.id.ingredientsTitle);
        titleRecipeDescription = getView().findViewById(R.id.titleRecipeDescription);
        recipeDescription = getView().findViewById(R.id.recipeDescription);

        //recipe metadata
        likes_and_views = getView().findViewById(R.id.likes_and_views);
        protein = getView().findViewById(R.id.protein);
        prepareTime = getView().findViewById(R.id.prepareTime);
        complexity = getView().findViewById(R.id.complexity);
        calories = getView().findViewById(R.id.calories);
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup any handles to view objects here
        ImageButton goBackButton = getView().findViewById(R.id.mockUpToolBarBackButton);
        button_like = getView().findViewById(R.id.button_like);
        if (userPressedLike) {
            button_like.setProgress(1);
        }
        likeButtonListener();
        goBackButton.setOnClickListener(v -> dismiss());
        initRecipeData();
        initializeUi();
    }

    /**
     * This method handles the case when the user presses or un presses like on the recipe
     */
    private void likeButtonListener() {
        button_like.setOnClickListener(view -> {
            if (!userPressedLike) {
                button_like.setProgress(0);
                button_like.playAnimation();
                userPressedLike = true;
                favoritesRef.document(recipe.getId()).set(recipe, SetOptions.merge());
            } else { //user pressed unlike
                button_like.setProgress(0);
                userPressedLike = false;
                favoritesRef.document(recipe.getId()).delete();
            }
        });
    }


    /**
     * set recipe layout and details: ingredients, instructions and metadata
     */
    private void initializeUi() {
        String views_str = recipe.getViews() + " peoples viewed this recipe";
        String preparationTime = "prep time:\n" + recipe.getPreparationTime();
        String complexity_str = "complexity:\n" + recipe.getDifficulty();
        String calories_str = "calories:\n" + recipe.getCalories();
        String protein_str = "protein:\n" + recipe.getProtein();

        try {
            Picasso.get().load(recipe.getImageUrl()).into(recipeImage);
        } catch (Exception e) {
        }

        recipeTitle.setText(recipe.getTitle());
        likes_and_views.setText(views_str);
        prepareTime.setText(preparationTime);
        complexity.setText(complexity_str);
        calories.setText(calories_str);
        protein.setText(protein_str);
        titleRecipeDescription.setText("Instructions");

        setIngredientsText(ingredientsTitle);
        setSteps();
    }

    /**
     * set the steps of the recipe and show them on the screen
     * built dynamically depends on the number of steps
     */
    private void setSteps() {
        //create linear layout params
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        int stepNum = 1;

        // for every step/instruction, show it on screen and add 1 to the number of steps
        for (String instruction : recipe.getInstructionsParts()) {
            TextView stepTitle = new TextView(recipeDescription.getContext());
            TextView stepDescription = new TextView(recipeDescription.getContext());

            stepTitle.setLayoutParams(lparams);
            stepTitle.setText("Step " + stepNum);

            TextViewCompat.setTextAppearance(stepTitle, R.style.fontForStepTitle);
            recipeDescription.addView(stepTitle);

            stepDescription.setLayoutParams(lparams);
            TextViewCompat.setTextAppearance(stepDescription, R.style.fontForStep);
            stepDescription.setText(instruction + "\n");
            stepDescription.setBackgroundResource(R.drawable.recipe_steps_background);
            recipeDescription.addView(stepDescription);

            stepNum += 1;
        }
    }

    /**
     * This method sets the ingredients of the recipe on screen
     *
     * @param ingredientsTitle the text view of the title "Ingredients" that comes before the
     *                         ingredients description
     */
    private void setIngredientsText(TextView ingredientsTitle) {
        LinearLayout layout = getView().findViewById(R.id.ingredients);
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ingredientsTitle.setText("Ingredients");
        List<String> ingredientsList = recipe.getExtendedIngredients();
        //check if the ingredients list is in legal size(not empty)
        if (ingredientsList.size() > 0) {
            for (String ingredient : ingredientsList) {
                TextView ingredientTextView = new TextView(getContext());
                ingredientTextView.setText("\u2022 " + ingredient);
                ingredientTextView.setLayoutParams(textViewLayoutParams);
                layout.addView(ingredientTextView);
                //we want to put a divider after each ingredient except the last one
                if (ingredientsList.indexOf(ingredient) < ingredientsList.size() - 1) {
                    addLineDivider(layout);
                }
            }
        }
    }

    /**
     * this method adds a line divider after each ingredient
     * @param layout the layout that contains the ingredients of the recipe
     */
    private void addLineDivider(LinearLayout layout) {
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));
        view.setBackgroundColor(Color.parseColor("#B3B3B3"));
        layout.addView(view);
    }


    /**
     * This method allows to create an instance of this fragment dialog,
     * and put whatever parameters you want in to a bundle that will be available when creating a
     * new instance.
     *
     * @param recipe          the specific recipe to represents
     * @param userPressedLike indicates weather the user liked this this recipe
     * @param activity        tha activity from which the recipe was called
     * @return a new instance of the dialog fragment with the given arguments
     */
    static RecipeFragment newInstance(Recipe recipe, boolean userPressedLike, int activity) {
        RecipeFragment instance = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        args.putBoolean("userPressedLike", userPressedLike);
        args.putInt("activity", activity);
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sourceActivity == SharedData.BOTTOM_NAV) {
            // only using this fragment with BottomNavigationBarActivity
            BottomNavigationBarActivity activity = (BottomNavigationBarActivity) getActivity();
            // after we exit the recipe fragment we will enable the Home\Favorites fragment.
            if (activity != null) {
                if (activity.favoritesFragment.isAdded()) {
                    activity.favoritesFragment.enableClickable();
                }
                if (activity.lastPushed == SharedData.DISCOVER) {
                    activity.discoverFragment.enableClickable();
                }
            }
        }
    }
}
