package com.example.ciy;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.jackandphantom.blurimage.BlurImage;

import java.util.ArrayList;
import java.util.Objects;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class SearchFragment extends Fragment {
    private AutoCompleteTextView userInput;
    private String[] languages = {"Shani ", "Carmel", "Lior", "Aviram", "Hagai", "Richi is the king"};
    private ArrayList<String> ingredients = new ArrayList<>();
    private TextView output;
    private boolean firstIngredient = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        userInput = Objects.requireNonNull(getView()).findViewById(R.id.enterIngredients);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, languages);
        userInput.setAdapter(adapter);
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        userInput.setTextColor(Color.DKGRAY);
        output = getView().findViewById(R.id.output);
        TextView headline = getView().findViewById(R.id.header);
        userInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private String input;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the input like for a normal EditText
                input = userInput.getText().toString();
                //update user entered ingredient in data and output his choice on screen
                ingredients.add(input);
                //clears search tab for next search
                userInput.setText("");
                //we want tu add a comma to output only after an ingredient != first
                if (firstIngredient) {
                    output.append(input);
                    firstIngredient = false;
                } else {
                    output.append(", " + input);

                }
                output.setVisibility(View.VISIBLE);
            }
        });


        float radius = 20f;

        View decorView = Objects.requireNonNull(getActivity()).getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();
        BlurView blurView = decorView.findViewById(R.id.blurView);
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getActivity()))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(false);
        ImageView background = getView().findViewById(R.id.background);
        BlurImage.with(getActivity()).load(R.drawable.background_kitchen).intensity(5).Async(true).into(background);
    }


    @RequiresApi(api = Build.VERSION_CODES.M) //TODO ???
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



}
