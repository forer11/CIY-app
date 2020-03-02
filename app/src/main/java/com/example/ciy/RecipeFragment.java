package com.example.ciy;


import com.airbnb.lottie.LottieAnimationView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.fragment.app.DialogFragment;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.List;


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

    private int openningActivity;

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
        openningActivity = getArguments().getInt("activity");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        favoritesRef = usersRef.document(user.getUid()).collection(SharedData.Favorites);

    }

    private void initRecipeData()
    {
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
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        initRecipeData();
        setRecipeView();
    }

    private void likeButtonListener() {
        button_like.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                if (!userPressedLike) {
                    button_like.setProgress(0);
                    button_like.playAnimation();
                    userPressedLike = true;
//                    Map<String, Object> newFavoriteRecipe = new HashMap<>();
//                    newFavoriteRecipe.put("id", recipe.getId());
                    favoritesRef.document(recipe.getId()).set(recipe, SetOptions.merge());
                    //activity.updateFavorites();
                    // TODO- add recipe to favorites
                } else { //user pressed unlike
                    button_like.setProgress(0);
                    userPressedLike = false;
                    favoritesRef.document(recipe.getId()).delete();
                    //activity.updateFavorites();
                    //TODO - remove recipe from favorites
                }
            }
        });
    }

    /**
     * set recipe layout and details: ingredients, instructions and metadata
     */
    private void setRecipeView() {

        initializeUi();
    }


    /**
     * init recipe data on the ui
     */
    private void initializeUi()
    {
        String views_str = recipe.getViews()+" peoples viewed this recipe";
        String prepration_str = "prep time:\n"+recipe.getPreparationTime();
        String complexity_str = "complexity:\n"+recipe.getDifficulty();
        String calories_str = "calories:\n"+recipe.getCalories();
        String protein_str = "protein:\n"+recipe.getProtein();

        try {
            Picasso.get().load(recipe.getImageUrl()).into(recipeImage);
        } catch (Exception e) {}

        recipeTitle.setText(recipe.getTitle());
        likes_and_views.setText(views_str);
        prepareTime.setText(prepration_str);
        complexity.setText(complexity_str);
        calories.setText(calories_str);
        protein.setText(protein_str);
        titleRecipeDescription.setText("Instructions");

        setIngredients(ingredientsTitle);

        setSteps();
    }

    /**
     * set the steps of the recipe and show them on the screen
     * built dynamically depends on the number of steps
     */
    private void setSteps()
    {
        //create linear layout params
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER;
        int stepNum = 1;

        // for every step/instruction, show it on screen and add 1 to the number of steps
        for(String instruction:recipe.getInstructionsParts())
        {
            TextView stepTitle = new TextView(recipeDescription.getContext());
            TextView stepDescription =new TextView(recipeDescription.getContext());

            stepTitle.setLayoutParams(lparams);
            stepTitle.setText("Step "+stepNum);
            stepTitle.setTextAppearance(getActivity(), R.style.fontForStepTitle);
            recipeDescription.addView(stepTitle);

            stepDescription.setLayoutParams(lparams);
            stepDescription.setTextAppearance(getContext(),R.style.fontForStep);
            stepDescription.setText(instruction+"\n");
            stepDescription.setBackgroundResource(R.drawable.recipe_steps_background);
            recipeDescription.addView(stepDescription);

            stepNum += 1;
        }
    }


    private void setIngredients(TextView ingredientsTitle) {//TODO carmel
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.ingredients);
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ingredientsTitle.setText("Ingredients");
        List<String> ingredientsList = recipe.getExtendedIngredients();
        if(ingredientsList.size()>0){
            for(String ingredient : ingredientsList){
                TextView ingredientTextView= new TextView(getContext());
                ingredientTextView.setText("\u2022 "+ingredient);
                ingredientTextView.setLayoutParams(textViewLayoutParams);
                layout.addView(ingredientTextView);
                if(ingredientsList.indexOf(ingredient)<ingredientsList.size()-1)
                {
                    addLineDivider(layout);
                }
            }
        }
    }

    private void addLineDivider(LinearLayout layout) {//TODO carmel
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));
        v.setBackgroundColor(Color.parseColor("#B3B3B3"));
        layout.addView(v);
    }

    // Creates a new fragment given an int and title
    static RecipeFragment newInstance (Recipe recipe,boolean userPressedLike, int activity){//TODO carmel
            RecipeFragment rec = new RecipeFragment();
            Bundle args = new Bundle();
            args.putSerializable("recipe", recipe);
            args.putBoolean("userPressedLike", userPressedLike);
            args.putInt("activity", activity);
            rec.setArguments(args);
            return rec;
        }

        @Override
        public void onDestroy () {//TODO carmel
            super.onDestroy();
            if (openningActivity == SharedData.BOTTOM_NAV) {
                // only using this fragment with BottomNavigationBar
                BottomNavigationBar activity = (BottomNavigationBar) getActivity();
                // after we exit the recipe fragment we will enable the Home\Favorites fragment.
                if (activity != null) {
                    if (activity.favoritesFragment.isAdded()) {
                        activity.favoritesFragment.enableClickable();
                    }
                    if (activity.lastPushed == SharedData.DISCOVER) {
                        activity.discoverFragment.enableClickable();
                    }
                } else {
                    // if this pops out we maybe opening the recipe fragment from an unexpected activity,
                    //TODO delete before submission
                    Toast.makeText(getContext(), "app Failure, current activity is " + getActivity(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
