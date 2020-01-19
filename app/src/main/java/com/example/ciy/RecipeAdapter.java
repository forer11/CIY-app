package com.example.ciy;


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


public class RecipeAdapter extends FirestoreRecyclerAdapter<Recipe, RecipeAdapter.NoteHolder> {
    private OnItemClickListener listener;

    public boolean isClickable = true;


    public RecipeAdapter(FirestoreRecyclerOptions<Recipe> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(NoteHolder noteHolder, int i, Recipe recipe) {
        noteHolder.textViewTitle.setText(recipe.getTitle());
        noteHolder.textViewDescription.setText(recipe.getDescription());
        noteHolder.textViewViews.setText(String.valueOf(recipe.getViews()) + " Views");
        try {
            Picasso.get()
                    .load(recipe.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(noteHolder.imageViewDish);
        } catch (Exception e) {
            noteHolder.imageViewDish.setImageResource(R.drawable.icon_dog_chef);
        }
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent, false);
        return new NoteHolder(v);
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewViews;
        ImageView imageViewDish;


        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewViews = itemView.findViewById(R.id.textViewViews);
            imageViewDish = itemView.findViewById(R.id.dishImage);

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
