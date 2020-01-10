package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.jackandphantom.blurimage.BlurImage;
import java.util.ArrayList;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class Searcher extends AppCompatActivity {

    private AutoCompleteTextView userInput;
    String[] languages = {"Shani ", "Carmel", "Lior", "Aviram", "Hagai", "Richi is the king"};
    ArrayList<String> ingredients = new ArrayList<>();
    TextView headline, output;
    ImageView background;
    boolean firstIngredient = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);
        userInput = findViewById(R.id.enterIngredients);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        userInput.setAdapter(adapter);
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        userInput.setTextColor(Color.DKGRAY);
        output = findViewById(R.id.output);
        headline = findViewById(R.id.header);
        userInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private String input;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the input like for a normal EditText
                input = userInput.getText().toString();
                //clears search tab for next search
                userInput.setText("");
                //update user entered ingredient in data and output his choice on screen
                ingredients.add(input);
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

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();
        BlurView blurView = decorView.findViewById(R.id.blurView);
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(false);
        background = findViewById(R.id.background);
        BlurImage.with(getApplicationContext()).load(R.drawable.background_kitchen).intensity(5).Async(true).into(background);
    }


}
