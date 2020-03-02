package com.example.ciy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    /* the current user's ingredients */
    private ArrayList<String> ingredients;
    /* useless for now */
    private OnItemClickListener searchListener;
    /* matching an ingredient with it corresponding photo*/
    private final HashMap<String, Integer> ingredientsImages;


    /**
     * click interfaces
     */
    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnLikeClick(int position);
    }

    /**
     * setting search listener
     *
     * @param searchListener the search listener
     */
    public void setOnItemClickListener(OnItemClickListener searchListener) {
        this.searchListener = searchListener;
    }

    /**
     * the ingredient items view holder
     */
    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        /* the ingredient name */
        TextView textViewIngredientName;
        /* the ingredient photo */
        ImageView imageViewIngredient;

        IngredientViewHolder(@NonNull View itemView, final OnItemClickListener searchListener) {
            super(itemView);
            textViewIngredientName = itemView.findViewById(R.id.textViewIngredientName);
            imageViewIngredient = itemView.findViewById(R.id.imageViewIngredient);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
    }

    /**
     * constructor for the Ingredients Adapter
     * @param ingredients the ingredients list
     * @param context the context of the activity we are using this adapter from
     */
    IngredientsAdapter(ArrayList<String> ingredients, Context context) {

        this.ingredients = ingredients;
        ingredientsImages = new HashMap<>();
        setImagesMap(ingredients, context);

    }

    /**
     * constructs a map of ingredients as keys with their photos as values.
     * @param ingredients the ingredients list
     * @param context the context of the activity we are using this adapter from
     */
    private void setImagesMap(ArrayList<String> ingredients, Context context) {
        String curIngredient;
        for (String ingredient : ingredients) {
            curIngredient = ingredient;
            // BBQ sauce is a 2 words image with capitals so it needs a spacial case.
            if (curIngredient.equals("BBQ sauce")) {
                curIngredient = "bbq_sauce";
            }
            if (curIngredient.contains(" ")) {
                curIngredient = curIngredient.replaceAll(" ", "_");
            }
            int resID = context.getResources().getIdentifier(curIngredient, "drawable", context.getPackageName());
            ingredientsImages.put(ingredient, resID);
        }
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_item,
                parent, false);
        return new IngredientViewHolder(view, searchListener);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = ingredients.get(position);

        holder.textViewIngredientName.setText(ingredient);
        holder.imageViewIngredient.setImageResource(ingredientsImages.get(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

}
