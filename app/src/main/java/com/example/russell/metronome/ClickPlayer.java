package com.example.russell.metronome;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.IOException;
import java.io.InputStream;

public class ClickPlayer {
    final int SAMPLE_RATE = 44100;
    private AudioTrack audioTrack;
    private byte[] initSoundArray;
    private byte[] soundArray;
    private byte[] silenceArray;
    private int soundLength;
    private int bpm = 10;

    public ClickPlayer() {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                SAMPLE_RATE,
                AudioTrack.MODE_STREAM);
        audioTrack.play();


    }

    public void setSound(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.wave_1);
            byte[] qweasd;
            initSoundArray = new byte[28800];

            soundLength = is.read(initSoundArray, 0, initSoundArray.length);

        } catch (IOException e){}
    }

    private int calcSilence() {
        int silenceLength = (int)(((60f/bpm)*SAMPLE_RATE*2-soundLength));
        if(silenceLength < 0) {
            soundArray = new byte[soundLength + silenceLength];
            System.arraycopy(initSoundArray, 0, soundArray, 0, soundArray.length);
            silenceLength = 0;
        } else {
            soundArray = initSoundArray;
        }
        silenceArray = new byte[silenceLength];
        return silenceLength;
    }

    public void play() {
        calcSilence();
        audioTrack.write(soundArray, 0, soundArray.length);
        audioTrack.write(silenceArray, 0, silenceArray.length);
    }

    public void setBPM(int bpm) {
        this.bpm = bpm;
    }
}
