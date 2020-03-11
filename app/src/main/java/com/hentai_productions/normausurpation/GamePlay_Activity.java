package com.hentai_productions.normausurpation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class GamePlay_Activity extends AppCompatActivity {

    public static String currentShipName, backgroundName;
    public static ShipObject currentShip;

    View decorView;
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    MediaPlayer BG_Sound_Player;
    public boolean isMusicEnabled = true;
    public AudioManager bgAudioManager;

    private AudioManager.OnAudioFocusChangeListener bgAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                BG_Sound_Player.pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                BG_Sound_Player.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener bgAudioCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    };

    private GamePlayView gamePlayView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play_);

        gamePlayView = findViewById(R.id.gamePlaySurfaceView);

        //Remove NavBar
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
        backgroundName = "level_1_background";

        if(isMusicEnabled)
        {
            bgAudioManager = (AudioManager) GamePlay_Activity.this.getSystemService(Context.AUDIO_SERVICE);
            assert bgAudioManager != null;
            int result = bgAudioManager.requestAudioFocus(bgAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // We have audio focus now.

                // Create and setup the {@link MediaPlayer} for the audio resource associated
                // with the current word
                BG_Sound_Player = MediaPlayer.create(this, R.raw.purple_planet_confrontation);

                // Start the audio file
                BG_Sound_Player.start();

                // Setup a listener on the media player, so that we can stop and release the
                // media player once the sound has finished playing.
                BG_Sound_Player.setOnCompletionListener(bgAudioCompletionListener);
            }
        }
    }


    @Override
    protected void onPause()
    {
        // stop the drawing to save cpu time
        gamePlayView.stopDrawThread();
        super.onPause();
        if(isMusicEnabled)
        {
            BG_Sound_Player.pause();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
        gamePlayView.startDrawThread();
        if(isMusicEnabled)
        {
            BG_Sound_Player.start();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backIntent = new Intent(GamePlay_Activity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }


    public static String getBackgroundName()
    {
        return backgroundName;
    }

    private void releaseMediaPlayer()
    {
        // If the media player is not null, then it may be currently playing a sound.
        if (BG_Sound_Player != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            BG_Sound_Player.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            BG_Sound_Player = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            bgAudioManager.abandonAudioFocus(bgAudioFocusChangeListener);
        }
    }
}