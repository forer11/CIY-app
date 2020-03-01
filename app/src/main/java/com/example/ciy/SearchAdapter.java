package com.example.ciy;

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
import java.util.Collections;
import java.util.Comparator;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {
    private ArrayList<Recipe> searchRecipes;
    private OnItemClickListener searchListener;
    /* 1 meaning by name , 2 meaning also by ingredients */
    private int filterType;


    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnLikeClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener searchListner) {
        this.searchListener = searchListner;
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewDish;
        public TextView textViewTitle;
        public TextView textViewDescription;
        public ImageView imageViewLike;
        public TextView textViewMatch;

        public SearchViewHolder(@NonNull View itemView, final OnItemClickListener searchListener) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.searchImageDish);
            textViewTitle = itemView.findViewById(R.id.searchTitle);
            textViewDescription = itemView.findViewById(R.id.searchDescription);
            textViewMatch = itemView.findViewById(R.id.searchMatch);

            imageViewLike = itemView.findViewById(R.id.searchLike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            searchListener.OnItemClick(position);
                        }
                    }
                }
            });

            imageViewLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            searchListener.OnLikeClick(position);
                        }
                    }
                }
            });
        }
    }

    public SearchAdapter(ArrayList<Recipe> searchRecipes, int filterType) {
        this.searchRecipes = searchRecipes;
        this.filterType = filterType;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new SearchViewHolder(view, searchListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Recipe recipe = searchRecipes.get(position);


        holder.textViewMatch.setText("");
        if (filterType == SharedData.INGREDIENTS_FILTER) {
            String format = String.format("%.1f", recipe.getMatchFactor() * 100) + "% Match";
            holder.textViewMatch.setText(format);
        }
        holder.textViewTitle.setText(recipe.getId());
        holder.textViewDescription.setText(recipe.getDescription());
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

    @Override
    public Filter getFilter() {
        if (filterType == SharedData.NAME_FILTER) {
            return searchFilter;
        } else {
            return searchByIngredientsFilter;
        }
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Recipe> filteredArrayList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredArrayList = new ArrayList<>(SharedData.searchRecipes);
                filteredArrayList = SharedData.activateFilters(filteredArrayList);

            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Recipe recipe : SharedData.searchRecipes) {
                    if (recipe.getTitle().toLowerCase().trim().contains(filterPattern)) {
                        filteredArrayList.add(recipe);
                    }
                }
                filteredArrayList = SharedData.activateFilters(filteredArrayList);
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

    private final Filter searchByIngredientsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Recipe> filteredArrayList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredArrayList = new ArrayList<>(SharedData.searchRecipes);
                filteredArrayList = SharedData.orderByIngredientsMatch(filteredArrayList);
                filteredArrayList = SharedData.activateFilters(filteredArrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Recipe recipe : SharedData.searchRecipes) {
                    if (recipe.getTitle().toLowerCase().trim().contains(filterPattern)) {
                        filteredArrayList.add(recipe);
                    }

                }
                filteredArrayList = SharedData.orderByIngredientsMatch(filteredArrayList);
                filteredArrayList = SharedData.activateFilters(filteredArrayList);
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


}
