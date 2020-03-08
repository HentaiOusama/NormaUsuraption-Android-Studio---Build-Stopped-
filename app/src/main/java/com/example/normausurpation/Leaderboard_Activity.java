package com.example.normausurpation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class Leaderboard_Activity extends AppCompatActivity {

    View decorView;
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(Leaderboard_Activity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }
}
