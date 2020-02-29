package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class Tags extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        List<Item> items = new ArrayList<>();
        for (int i=0;i<10;i++)
        {
            items.add(new Item("item "+ i, "Items "+i));
        }
        CollectionPicker picker = (CollectionPicker) findViewById(R.id.orderFilters);
        picker.setItems(items);
        picker.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(Item item, int position) {

            }
        });


    }
}
