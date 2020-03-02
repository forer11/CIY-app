package com.example.ciy;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

/**
 * This class represents the Fridge fragment, which allows the user to see the ingredients he added
 * and remove them if he wish to
 */
public class FridgeFragment extends DialogFragment {

    /* the user's current ingredients */
    private ArrayList<String> ingredients;

    /* the ingredients recycleView adapter */
    private IngredientsAdapter ingredientsAdapter;

    /* the ingredients that the user has deleted from the fridge*/
    private ArrayList<String> removed;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fridge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ingredients = SharedData.ingredients;
        // sets up the recyclerView adapter and swipe option
        setUpRecyclerView();
        // sets up the opening doors animation when the fridge fragment is opened
        LottieAnimationView fridgeDoorsOpen = view.findViewById(R.id.fridgeDoorsOpen);
        fridgeDoorsOpen.setProgress(0);
        fridgeDoorsOpen.playAnimation();
        // sets up the cookie chef animation when the fridge fragment is opened
        LottieAnimationView cookieChef = view.findViewById(R.id.mr_cookie);
        cookieChef.setProgress(0);
        cookieChef.playAnimation();
        cookieChef.setOnClickListener(view1 -> dismiss());
        removed = new ArrayList<>();
    }

    /**
     * sets the recycler view that represents the ingredients that is in the fridge
     */
    private void setUpRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.ingredientsRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        ingredientsAdapter = new IngredientsAdapter(ingredients, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ingredientsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        setItemTouchHelpers(recyclerView);
    }

    /**
     * handles the recycle view elements in cases of move or swipe
     * @param recyclerView the recycler view to handle
     */
    private void setItemTouchHelpers(final RecyclerView recyclerView) {
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
                removed.add(ingredients.get(position));
                ingredients.remove(position);
                ingredientsAdapter.notifyItemRemoved(position);
                recyclerView.scheduleLayoutAnimation();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        window.setLayout(width - 250, height - 400);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((BottomNavigationBarActivity) getActivity()).homeFragment.updateBadge();
        ((BottomNavigationBarActivity) getActivity()).homeFragment.updateBasicIngredients(removed);
    }
}
