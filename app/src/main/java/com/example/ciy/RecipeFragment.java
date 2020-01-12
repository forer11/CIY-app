package com.example.ciy;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.fragment.app.Fragment;


import com.squareup.picasso.Picasso;


public class RecipeFragment extends Fragment  {


    private Recipe recipe;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        recipe = (Recipe) getArguments().getSerializable("recipe");
    }

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup any handles to view objects here
        TextView recipeTitle = getView().findViewById(R.id.recipeTitle);
        ImageView recipeImage = getView().findViewById(R.id.recipeImage);
        TextView titleRecipeIngredients= getView().findViewById(R.id.titleRecipeIngredients);
        TextView recipeIngredients= getView().findViewById(R.id.recipeIngredients);
        TextView titleRecipeDescription= getView().findViewById(R.id.titleRecipeDescription);
        TextView recipeDescription= getView().findViewById(R.id.recipeDescription);
        TextView recipeViews= getView().findViewById(R.id.recipeViews);

        recipeTitle.setText(recipe.getTitle());
        try {
            Picasso.get()
                    .load(recipe.getImageUrl()).into(recipeImage);
        } catch (Exception e) {
            recipeImage.setImageResource(R.drawable.icon_dog_chef);
        }
        titleRecipeIngredients.setText("Ingredients");
        for (String ingredient:recipe.getIngredients()) {
            recipeIngredients.append("* "+ingredient+"\n");
        }
        titleRecipeDescription.setText("Description");
        recipeDescription.setText(" "+recipe.getDescription());
        recipeViews.setText("Views: "+recipe.getViews());
    }


    // Creates a new fragment given an int and title
    public static RecipeFragment newInstance(Recipe recipe) {
        RecipeFragment rec = new RecipeFragment();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        rec.setArguments(args);
        return rec;
    }
}
