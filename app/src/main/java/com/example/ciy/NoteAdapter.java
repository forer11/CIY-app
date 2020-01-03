package com.example.ciy;


import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>{

    public NoteAdapter(FirestoreRecyclerOptions<Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(NoteHolder noteHolder, int i, Note note) {
        noteHolder.textViewTitle.setText(note.getTitle());
        noteHolder.textViewDescription.setText(note.getDescription());
        noteHolder.textViewPriority.setText(String.valueOf(note.getPriority()));
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent,false);
        return new NoteHolder(v);
    }

    class  NoteHolder extends RecyclerView.ViewHolder{
        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPriority;


        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewPriority = itemView.findViewById(R.id.textViewPriority);
        }
    }
}
