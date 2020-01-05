package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jackandphantom.blurimage.BlurImage;
import com.jgabrielfreitas.core.BlurImageView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class Blurred extends AppCompatActivity {

    ImageButton button;
    ImageView background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blurred);

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


        button = findViewById(R.id.imageButton);
        BlurImage.with(getApplicationContext()).load(R.id.imageButton).intensity(25).Async(false).into(button);

    }
}
