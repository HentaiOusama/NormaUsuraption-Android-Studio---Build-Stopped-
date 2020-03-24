package com.hentai_productions.normausurpation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Splash_Activity extends AppCompatActivity {

    View decorView;
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(Splash_Activity.this, MainActivity.class);
                    startActivity(intent);
                    Splash_Activity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);

    }
}