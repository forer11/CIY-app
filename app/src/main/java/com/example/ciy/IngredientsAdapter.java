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
import java.util.Map;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>
        implements Filterable {

    /* the current user's ingredients */
    private ArrayList<String> ingredients;
    /* useless for now */
    private OnItemClickListener searchListener;
    private final HashMap<String, Integer> ingresientsImages;


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
    public static class IngredientViewHolder extends RecyclerView.ViewHolder {
        /* the ingredient name */
        public TextView textViewIngredientName;
        /* tbd TODO Lior */
        public ImageView imageViewIngredient;

        public IngredientViewHolder(@NonNull View itemView, final OnItemClickListener searchListener) {
            super(itemView);
            textViewIngredientName = itemView.findViewById(R.id.textViewIngredientName);
            imageViewIngredient = itemView.findViewById(R.id.imageViewIngredient);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (searchListener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            searchListener.OnItemClick(position);
//                        }
//                    }
                }
            });

//            imageViewIngredient.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (searchListener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            searchListener.OnLikeClick(position);
//                        }
//                    }
//                }
//            });
        }
    }

    public IngredientsAdapter(ArrayList<String> ingredients, Context context) {

        this.ingredients = ingredients;
        ingresientsImages = new HashMap<>();
        setImagesMap(ingredients, context);

    }

    private void setImagesMap(ArrayList<String> ingredients, Context context) {
        String curIngredient;
        for (String ingredient : ingredients) {
            curIngredient = ingredient;
//            if (curIngredient.contains(" ")) {
//                curIngredient = curIngredient.replaceAll(" ", "_");
//            }
            int resID = context.getResources().getIdentifier(curIngredient, "drawable", context.getPackageName());
            ingresientsImages.put(ingredient, resID);
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
        holder.imageViewIngredient.setImageResource(R.drawable.salt);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<String> filteredArrayList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredArrayList.clear();
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (String ingredient : SharedData.allIngredients) {
                    if (ingredient.toLowerCase().trim().contains(filterPattern)) {
                        filteredArrayList.add(ingredient);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredArrayList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ingredients.clear();
            ingredients.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
