package com.example.normausurpation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;

public class GamePlay_Activity extends AppCompatActivity {

    public static String currentShipName, backgroundName;

    View decorView;
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;



    private GamePlayView gamePlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play_);

        gamePlayView = (GamePlayView) findViewById(R.id.gamePlaySurfaceView);

        //Remove NavBar
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
        currentShipName = "sp_ship_1";
        backgroundName = "level_1_background";

    }




    @Override
    protected void onPause()
    {
        // stop the drawing to save cpu time
        gamePlayView.stopDrawThread();
        super.onPause();
    }





    @Override
    protected void onResume()
    {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
        gamePlayView.startDrawThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(GamePlay_Activity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }



    public static String getCurrentShipName()
    {
        return currentShipName;
    }

    public static String getBackgroundName()
    {
        return backgroundName;
    }
}
