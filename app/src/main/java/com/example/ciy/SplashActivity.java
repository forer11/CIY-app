package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //launch the next screen after delay

        handler = new Handler();
        callback = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };
            handler.postDelayed(callback, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(callback);
    }
}
