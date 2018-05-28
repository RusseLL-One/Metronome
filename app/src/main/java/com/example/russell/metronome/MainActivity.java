package com.example.russell.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button playButton;
    RotaryKnob knob;
    ClickPlayerTask clickPlayerTask;
    SeekBar volumeBar;
    private AudioManager audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.debugTextView);
        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(buttonClickListener);

        knob = new RotaryKnob(this);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeBar = (SeekBar) findViewById(R.id.volumebar);
        volumeBar.setMax(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setProgress(audio.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeBar.setOnSeekBarChangeListener(volumeListener);
    }

    View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(clickPlayerTask == null || !clickPlayerTask.isPlaying()) {
                playButton.setText(R.string.buttonStop);
                clickPlayerTask = new ClickPlayerTask(knob.getBpm());
                knob.setPlayerTask(clickPlayerTask);
                clickPlayerTask.getSound(MainActivity.this);
                clickPlayerTask.execute();
            } else {
                playButton.setText(R.string.buttonPlay);
                clickPlayerTask.stop();
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

            audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }

    };

    @Override
    public boolean onKeyUp(int keycode, KeyEvent e) {
        short volume = (short) audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch(keycode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                volumeBar.setProgress(volume);
                break;
        }

        return super.onKeyUp(keycode, e);
    }
}
