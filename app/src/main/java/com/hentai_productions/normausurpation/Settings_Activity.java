package com.hentai_productions.normausurpation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Settings_Activity extends AppCompatActivity {

    View decorView;
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(Settings_Activity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }
}