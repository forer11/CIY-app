package com.example.ciy;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

/**
 * adapter for the Discover and Favorites RecyclerView, this adapter syncs directly with the fireStore data base
 */
public class RecipeAdapter extends FirestoreRecyclerAdapter<Recipe, RecipeAdapter.RecipeHolder> {
    private OnItemClickListener listener;

    /* represents the layout of the Favorites Fragment or the Discover Fragment */
    private int layout;

    /* indicates if we can click the Recycler view */
    boolean isClickable = true;


    RecipeAdapter(FirestoreRecyclerOptions<Recipe> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull RecipeHolder recipeHolder,
                                    int i,
                                    @NonNull Recipe recipe) {
        if (layout == R.layout.recipe_item) {
            setRecipeItemLayout(recipeHolder, recipe);
        } else if (layout == R.layout.favorite_item) {
            recipeHolder.textViewTitle.setText(recipe.getTitle());
            // tries to upload the image url, else sets a default one
            try {
                Picasso.get()
                        .load(recipe.getImageUrl())
                        .fit()
                        .centerCrop()
                        .into(recipeHolder.circularImageViewDish);
            } catch (Exception e) {
                recipeHolder.circularImageViewDish.setImageResource(R.drawable.icon_dog_chef);
            }

        }
    }

    /**
     * sets the layout for a recipe in the DiscoverFragment
     * @param recipeHolder represents a lone in the recyclerView
     * @param recipe the current recipe
     */
    private void setRecipeItemLayout(RecipeHolder recipeHolder, Recipe recipe) {
        recipeHolder.textViewTitle.setText(recipe.getTitle());
        recipeHolder.textViewDescription.setText(recipe.getDescription());
        String viewsText = (recipe.getViews() + " Views");
        recipeHolder.textViewViews.setText(viewsText);
        // tries to upload the image url, else sets a default one
        try {
            Picasso.get()
                    .load(recipe.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(recipeHolder.imageViewDish);
        } catch (Exception e) {
            recipeHolder.imageViewDish.setImageResource(R.drawable.icon_dog_chef);
        }
    }


    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,
                parent, false);
        return new RecipeHolder(v);
    }

    /**
     * if needed we can delete items with this method
     * @param position the position of the item we want to delete
     */
    void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    /**
     * class representing a line of the recyclerView
     */
    class RecipeHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewViews;
        ImageView imageViewDish;
        ImageView circularImageViewDish;


        RecipeHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewViews = itemView.findViewById(R.id.textViewViews);
            imageViewDish = itemView.findViewById(R.id.dishImage);
            circularImageViewDish = itemView.findViewById(R.id.circularImageViewDish);

            itemView.setOnClickListener(v -> {
                if (isClickable) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }

            });
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
}
