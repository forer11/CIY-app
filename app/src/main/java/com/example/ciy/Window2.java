package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class Window2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(ContextCompat.getDrawable(this, R.drawable.logo_chef));
        toolbar.setTitle(R.string.app_title);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_buttons,menu);
        menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.shani));
        //TODO here we can set the button image !!
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int res_id = item.getItemId();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Window2.this);

        mBuilder.setTitle("Hi you");
        mBuilder.setMessage("Wer'e sorry to see you go");
        mBuilder.setCancelable(false)
                .setPositiveButton("sign out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Window2.this, signInActivity.class));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        final AlertDialog alertdialog = mBuilder.create();
        alertdialog.show();

        alertdialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);


        return super.onOptionsItemSelected(item);
    }
}
