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


public class RecipeAdapter extends FirestoreRecyclerAdapter<Recipe, RecipeAdapter.RecipeHolder> {
    private OnItemClickListener listener;

    private int layout;

    /* indicates if we can click the Recycler view */
    boolean isClickable = true;


    RecipeAdapter(FirestoreRecyclerOptions<Recipe> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(RecipeHolder recipeHolder, int i, Recipe recipe) {
        if (layout == R.layout.recipe_item) {
            setRecipeItemLayout(recipeHolder, recipe);
        } else if (layout == R.layout.favorite_item) {
            recipeHolder.textViewTitle.setText(recipe.getTitle());
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

    private void setRecipeItemLayout(RecipeHolder recipeHolder, Recipe recipe) {
        recipeHolder.textViewTitle.setText(recipe.getTitle());
        recipeHolder.textViewDescription.setText(recipe.getDescription());
        recipeHolder.textViewViews.setText(recipe.getViews() + " Views");
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

    void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickable) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && listener != null) {
                            listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                        }
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
