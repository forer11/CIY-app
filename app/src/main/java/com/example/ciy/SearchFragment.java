package com.example.ciy;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * This class represents the Search fragment, which allows the user to type ingredients he has at
 * home, and get ingredient suggestions from our data base while doing so. The user can add as
 * many ingredients as he wishes, and can edit them afterwards (i.e- delete them).
 */
public class SearchFragment extends Fragment {

    private AutoCompleteTextView userInput;
    private ArrayList<String> ingredients = new ArrayList<>();
    private TextView ingredientName;
    private boolean firstIngredient = true;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        final List<String> ingredientOptions = new ArrayList<>();
//        ingredientsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                //we add all ingredients from our data base to 'ingredientOptions' list
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    String option = documentSnapshot.get("ingredient").toString(); //TODO CHECK VALIDITY
//                    ingredientOptions.add(option);
//                }
//                adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
//                        android.R.layout.simple_list_item_1, ingredientOptions);
//                setUserInput();
//            }
//        });
    }

    private void setUserInput() {
        userInput = Objects.requireNonNull(getView()).findViewById(R.id.enterIngredients);
        userInput.setAdapter(adapter);
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        userInput.setTextColor(Color.DKGRAY);
        ingredientName = getView().findViewById(R.id.output);
        userInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String input;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the input like for a normal EditText
                input = userInput.getText().toString();
                //update user entered ingredient in data and ingredientName his choice on screen
                ingredients.add(input);
                //clears search tab for next search
                userInput.setText("");

                //TODO - create a recycler view for new ingredient
//                if (firstIngredient) {
//                    ingredientName.append(input);
//                    firstIngredient = false;
//                } else {
//                    ingredientName.append(", " + input);
//                }
//                ingredientName.setVisibility(View.VISIBLE);
            }
        });
        blurIngredientsView();
    }

    private void blurIngredientsView() {
        float radius = 20f;
        View decorView = Objects.requireNonNull(getActivity()).getWindow().getDecorView();
        //ViewGroup we want to start blur from.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame.
        Drawable windowBackground = decorView.getBackground();
        BlurView blurView = decorView.findViewById(R.id.blurView);
        blurView.setupWith(rootView).setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getActivity())).setBlurRadius(radius)
                .setHasFixedTransformationMatrix(false);
        ImageView background = getView().findViewById(R.id.background);
        BlurImage.with(getActivity()).load(R.drawable.background_kitchen).intensity(5).
                Async(true).into(background);
    }
}
