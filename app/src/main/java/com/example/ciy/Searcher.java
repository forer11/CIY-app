package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class Searcher extends AppCompatActivity {

    AutoCompleteTextView text;
    String[] languages = {"Shani ", "Carmel", "Lior", "Aviram", "Hagai", "Richi is the king"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);

        text = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, languages);
        text.setAdapter(adapter);
        text.setThreshold(1);
        text.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        text.setTextColor(Color.DKGRAY);

    }

}
