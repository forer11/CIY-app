package com.example.ciy;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter Class for the search mechanism where we apply filters, and show them on the recyclerView
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>
        implements Filterable {

    /* the recipes results of the current search */
    private ArrayList<Recipe> searchRecipes;
    /* listener for the search items */
    private OnItemClickListener searchListener;
    /* 1 meaning by name(normal) , 2 meaning also by ingredients */
    private int filterType;


    /**
     * the item click listener interface where we check for clicks on the recyclerView items.
     */
    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnLikeClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener searchListener) {
        this.searchListener = searchListener;
    }

    /**
     * this class represent the view of each line in the recycler view
     */
    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewDish;
        TextView textViewTitle;
        TextView textViewDescription;
        ImageView imageViewLike;
        TextView textViewMatch;

        SearchViewHolder(@NonNull View itemView, final OnItemClickListener searchListener) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.searchImageDish);
            textViewTitle = itemView.findViewById(R.id.searchTitle);
            textViewDescription = itemView.findViewById(R.id.searchDescription);
            textViewMatch = itemView.findViewById(R.id.searchMatch);

            imageViewLike = itemView.findViewById(R.id.searchLike);

            itemView.setOnClickListener(v -> {
                if (searchListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        searchListener.OnItemClick(position);
                    }
                }
            });

            imageViewLike.setOnClickListener(v -> {
                if (searchListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        searchListener.OnLikeClick(position);
                    }
                }
            });
        }
    }

    /**
     * the search adapter constructor, where we choose the filter type and the set the recipe
     * List.
     *
     * @param searchRecipes the recipe List
     * @param filterType    the type of filter the user currently use.
     */
    SearchAdapter(ArrayList<Recipe> searchRecipes, int filterType) {
        this.searchRecipes = searchRecipes;
        this.filterType = filterType;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item,
                parent, false);
        return new SearchViewHolder(view, searchListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Recipe recipe = searchRecipes.get(position);

        // here we set the Match title to be nothing if the Ingredients filter isn't activated
        // and to be the match percentages otherwise.
        holder.textViewMatch.setText("");
        if (filterType == SharedData.INGREDIENTS_FILTER) {
            @SuppressLint("DefaultLocale") String format =
                    String.format("%.1f", recipe.getMatchFactor() * 100) + "% Match";
            holder.textViewMatch.setText(format);
        }
        holder.textViewTitle.setText(recipe.getId());
        holder.textViewDescription.setText(recipe.getDescription());

        // here we try to set the recipe image with a url, if failure occurs we set default image.
        try {
            Picasso.get()
                    .load(recipe.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.imageViewDish);
        } catch (Exception e) {
            holder.imageViewDish.setImageResource(R.drawable.icon_dog_chef);
        }
    }

    @Override
    public int getItemCount() {
        return searchRecipes.size();
    }

    /**
     * returns the current filter the adapter works with
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        if (filterType == SharedData.NAME_FILTER) {
            return searchFilter;
        } else {
            return searchByIngredientsFilter;
        }
    }

    /**
     * filters by the user search and activated filters.
     */
    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Recipe> filteredArrayList = new ArrayList<>();

            // in case a bad search was entered or there is no search parameter.
            if (constraint == null || constraint.length() == 0) {
                filteredArrayList = new ArrayList<>(SharedData.searchRecipes);
                SharedData.applyFilters(filteredArrayList);

            } else {
                filterSearchResults(constraint, filteredArrayList);
                SharedData.applyFilters(filteredArrayList);
            }

            FilterResults results = new FilterResults();
            results.values = filteredArrayList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchRecipes.clear();
            searchRecipes.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * filters the search results by their match to the user's ingredients list by descending order,
     * also applies the other filters
     */
    private final Filter searchByIngredientsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Recipe> filteredArrayList = new ArrayList<>();

            // in case a bad search was entered or there is no search parameter.
            if (constraint == null || constraint.length() == 0) {
                filteredArrayList = new ArrayList<>(SharedData.searchRecipes);
                SharedData.orderByIngredientsMatch(filteredArrayList);
                SharedData.applyFilters(filteredArrayList);
            } else {
                filterSearchResults(constraint, filteredArrayList);
                SharedData.orderByIngredientsMatch(filteredArrayList);
                SharedData.applyFilters(filteredArrayList);
            }

            FilterResults results = new FilterResults();
            results.values = filteredArrayList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchRecipes.clear();
            searchRecipes.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * filter the recipes by the user's search parameter.
     * @param constraint the user search parameter
     * @param filteredArrayList the filtered recipe list
     */
    private void filterSearchResults(CharSequence constraint, ArrayList<Recipe> filteredArrayList) {
        String filterPattern = constraint.toString().toLowerCase().trim();

        for (Recipe recipe : SharedData.searchRecipes) {
            if (recipe.getTitle().toLowerCase().trim().contains(filterPattern)) {
                filteredArrayList.add(recipe);
            }
        }
    }


}
