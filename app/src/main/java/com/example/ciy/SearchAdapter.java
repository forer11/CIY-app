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
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {
    private ArrayList<Recipe> searchRecipes;
    private OnItemClickListener searchListener;


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

        public SearchViewHolder(@NonNull View itemView, final OnItemClickListener searchListener) {
            super(itemView);
            imageViewDish = itemView.findViewById(R.id.searchImageDish);
            textViewTitle = itemView.findViewById(R.id.searchTitle);
            textViewDescription = itemView.findViewById(R.id.searchDescription);
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

    public SearchAdapter(ArrayList<Recipe> searchRecipes) {
        this.searchRecipes = searchRecipes;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(view, searchListener);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Recipe recipe = searchRecipes.get(position);

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
        return searchFilter;
    }

    private final Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Recipe> filteredArrayList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredArrayList.addAll(SharedData.searchRecipes);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Recipe recipe : SharedData.searchRecipes) {
                    if (recipe.getTitle().toLowerCase().trim().contains(filterPattern)) {
                        filteredArrayList.add(recipe);
                    }
                }
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
