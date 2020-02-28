package com.example.ciy;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the Search fragment, which allows the user to type ingredients he has at
 * home, and get ingredient suggestions from our data base while doing so. The user can add kitchen
 * many ingredients kitchen he wishes, and can edit them afterwards (splashscreen_background.e- delete them).
 */
public class SearchFragment extends DialogFragment {

    /* the autoComplete object for the possible ingredients */
    private AutoCompleteTextView userInput;
    /* the user's current ingredients */
    private ArrayList<String> ingredients;
    /* the firestore data base instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* the firestore ingredients collection reference */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);
    /* the search option adapter */
    private ArrayAdapter<String> searchOptionsAdapter;
    /* the ingredients recycleView adapter */
    private IngredientsAdapter ingredientsAdapter;
    /* the search options ingredients list */
    private List<String> ingredientOptions;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ingredients = SharedData.ingredients;
        // sets up the recyclerView adapter and swipe option
        setUpRecyclerView();
        // sets up the auto fill search adapter and data
        setUpSearchAdapter();
        LottieAnimationView fridgeDoorsOpen = view.findViewById(R.id.fridgeDoorsOpen);
        fridgeDoorsOpen.setProgress(0);
        fridgeDoorsOpen.playAnimation();
    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ingredientsAdapter = new IngredientsAdapter(ingredients);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ingredientsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        setItemTouchHelpers(recyclerView);
    }

    private void setItemTouchHelpers(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView
                    .ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ingredientOptions.add(ingredients.get(position));
                ingredients.remove(position);
                ingredientsAdapter.notifyItemRemoved(position);
                searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1, ingredientOptions);
                userInput.setAdapter(searchOptionsAdapter);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setUpSearchAdapter() {
        final Context context = getActivity();
        final View view = getView();
        if (SharedData.allIngredients.isEmpty()) {
            ingredientsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //we add all ingredients from our data base to 'ingredientOptions' list
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String option = documentSnapshot.get("ingredient").toString(); //TODO CHECK VALIDITY
                        SharedData.allIngredients.add(option);
                    }
                    ingredientOptions = new ArrayList<>(SharedData.allIngredients);
                    searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                            android.R.layout.simple_list_item_1, ingredientOptions);
                    setUserInput(view);
                }
            });
        } else {
            searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                    android.R.layout.simple_list_item_1, ingredientOptions);
            setUserInput(view);
        }
    }

    private void setUserInput(View view) {
        userInput = view.findViewById(R.id.enterIngredients);
        userInput.setAdapter(searchOptionsAdapter);
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        userInput.setTextColor(Color.DKGRAY);
        userInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String input;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the input like for a normal EditText
                input = userInput.getText().toString();
                //update user entered ingredient in data and ingredientName his choice on screen
                ingredients.add(input);
                ingredientsAdapter.notifyItemInserted(ingredients.size() - 1);
                ingredientOptions.remove(input);
                // notify data has changed don't work for android adapter, known issue online.
                searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                        android.R.layout.simple_list_item_1, ingredientOptions);
                userInput.setAdapter(searchOptionsAdapter);
                //clears search tab for next search
                userInput.setText("");
            }
        });
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width - 250, height - 400);
        window.setGravity(Gravity.CENTER);
        //TODO:
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((BottomNavigationBar) getActivity()).homeFragment.updateBadge();
    }
}
