package com.example.ciy;


import com.airbnb.lottie.LottieAnimationView;

import android.graphics.Color;
import android.os.Bundle;
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

    private void setRecipeView() {
        TextView recipeTitle = getView().findViewById(R.id.recipeTitle);
        ImageView recipeImage = getView().findViewById(R.id.recipeImage);
        TextView ingredientsTitle = getView().findViewById(R.id.ingredientsTitle);
        TextView prepareTime = getView().findViewById(R.id.prepareTime);
        TextView titleRecipeDescription = getView().findViewById(R.id.titleRecipeDescription);
        TextView recipeDescription = getView().findViewById(R.id.recipeDescription);
        initializeUi(recipeTitle, recipeImage, prepareTime,ingredientsTitle,
                titleRecipeDescription, recipeDescription, button_like);
    }

//    private void blurBackBackground() {
//        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
//                R.drawable.n);
//        Bitmap blurredBitmap = BlurBuilder.blur( getActivity(), icon );
//
//        getView().setBackground( new BitmapDrawable( getResources(), blurredBitmap ) );
    //TODO decide
//        final Activity activity = getActivity();
//        final View content = activity.findViewById(android.R.id.content).getRootView();
//        if (content.getWidth() > 0) {
//            Bitmap image = BlurBuilder.blur(content);
//            getView().setBackground(new BitmapDrawable(activity.getResources(), image));
//        } else {
//            content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    Bitmap image = BlurBuilder.blur(content);
//                    getView().setBackground(new BitmapDrawable(activity.getResources(), image));
//                }
//            });
//        }
//    }

    private void initializeUi(TextView recipeTitle, ImageView recipeImage,
                              TextView prepareTime, TextView ingredientsTitle,
                              TextView titleRecipeDescription, TextView recipeDescription,
                              LottieAnimationView button_like) {
        recipeTitle.setText(recipe.getTitle());
        try {
            Picasso.get()
                    .load(recipe.getImageUrl()).into(recipeImage);
        } catch (Exception e) {
            recipeImage.setImageResource(R.drawable.icon_dog_chef);
        }
        //TODO - update to real time from db
        prepareTime.setText("\uD83D\uDD52 30 " + "min  " + "\uD83D\uDC69\u200D\uD83C\uDF73 "
                + recipe.getViews() + " views");
        setIngredients(ingredientsTitle);
            titleRecipeDescription.setText("Description");
            recipeDescription.setText(recipe.getInstructions());
    }

    private void setIngredients(TextView ingredientsTitle) {
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

    private void addLineDivider(LinearLayout layout) {
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        ));
        v.setBackgroundColor(Color.parseColor("#B3B3B3"));
        layout.addView(v);
    }

    // Creates a new fragment given an int and title
        static RecipeFragment newInstance (Recipe recipe,boolean userPressedLike, int activity){
            RecipeFragment rec = new RecipeFragment();
            Bundle args = new Bundle();
            args.putSerializable("recipe", recipe);
            args.putBoolean("userPressedLike", userPressedLike);
            args.putInt("activity", activity);
            rec.setArguments(args);
            return rec;
        }

        @Override
        public void onDestroy () {
            super.onDestroy();
            if (openningActivity == SharedData.BOTTOM_NAV) {
                // only using this fragment with BottomNavigationBar
                BottomNavigationBar activity = (BottomNavigationBar) getActivity();
                // after we exit the recipe fragment we will enable the Home\Favorites fragment.
                if (activity != null) {
                    if (activity.favoritesFragment.isAdded()) {
                        activity.favoritesFragment.enableClickable();
                    }
                    if (activity.lastPushed == SharedData.HOME) {
                        activity.homeFragment.enableClickable();
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
