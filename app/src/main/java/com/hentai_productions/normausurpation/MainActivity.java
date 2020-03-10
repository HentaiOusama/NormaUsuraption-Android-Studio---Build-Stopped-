package com.hentai_productions.normausurpation;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Intent Play_Intent, Options_Intent, Leaderboard_Intent, Settings_Intent;
    View decorView;
    public int intentNumber = 0;

    //flags to hide navigation bar
    int uiOptionsForDevicesWithoutNavBar = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;


    //Code for playing background Music
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


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Code to hide Navigation bar
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);


        //Intents to go to different activities
        Play_Intent = new Intent(MainActivity.this, GamePlay_Activity.class);
        Options_Intent = new Intent(MainActivity.this, Option_Activity.class);
        Leaderboard_Intent = new Intent(MainActivity.this, Leaderboard_Activity.class);
        Settings_Intent = new Intent(MainActivity.this, Settings_Activity.class);


        // Below Code is for Playing BG Music
        if(isMusicEnabled)
        {
            bgAudioManager = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
            assert bgAudioManager != null;
            int result = bgAudioManager.requestAudioFocus(bgAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                // We have audio focus now.

                // Create and setup the {@link MediaPlayer} for the audio resource associated
                // with the current word
                BG_Sound_Player = MediaPlayer.create(this, R.raw.epic_ride_bensound);

                // Start the audio file
                BG_Sound_Player.start();

                // Setup a listener on the media player, so that we can stop and release the
                // media player once the sound has finished playing.
                BG_Sound_Player.setOnCompletionListener(bgAudioCompletionListener);
            }
        }

        final Button playButton, optionsButton, leaderboardButton, settingsButton;
        final Animation animator = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink_anim);
        final Animation animator1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink_anim);
        final Animation animator2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shrink_anim);
        final Animation animatorSettings = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);

        playButton = findViewById(R.id.Play_Button);
        optionsButton = findViewById(R.id.Options_Button);
        leaderboardButton = findViewById(R.id.Leaderboards_Button);
        settingsButton = findViewById(R.id.settings_button);


        animator.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animator1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animator2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animatorSettings.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeActivity();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.startAnimation(animator);
                intentNumber = 1;
                onEnterAnimationComplete();
            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsButton.startAnimation(animator1);
                intentNumber = 2;
                onEnterAnimationComplete();
            }
        });

        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaderboardButton.startAnimation(animator2);
                intentNumber = 3;
                onEnterAnimationComplete();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsButton.startAnimation(animatorSettings);
                intentNumber = 4;
                onEnterAnimationComplete();
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        decorView.setSystemUiVisibility(uiOptionsForDevicesWithoutNavBar);
        if(isMusicEnabled)
        {
            BG_Sound_Player.start();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(isMusicEnabled)
        {
            BG_Sound_Player.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }


    public void changeActivity()
    {
        switch (intentNumber)
        {
            case 1 :
                Start_Game();
                break;

            case 2 :
                Open_Option();
                break;

            case 3 :
                Show_Leaderboard();
                break;

            case 4 :
                Open_Settings();
                break;
        }
    }

    public void Start_Game()
    {
        startActivity(Play_Intent);
        finish();
    }

    public void Open_Option()
    {
        startActivity(Options_Intent);
        finish();
    }

    public void Show_Leaderboard()
    {
        startActivity(Leaderboard_Intent);
        finish();
    }

    public void Open_Settings()
    {
        startActivity(Settings_Intent);
        finish();
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