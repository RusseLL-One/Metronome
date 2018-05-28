package com.example.russell.metronome;

import android.content.Context;
import android.os.AsyncTask;

public class ClickPlayerTask extends AsyncTask {
    private ClickPlayer player = new ClickPlayer();
    private boolean isPlaying = false;

    public ClickPlayerTask(int bpm) {
        player.setBPM(bpm);
    }

    public void setBPM(int bpm) {
        player.setBPM(bpm);
    }

    public void getSound(Context context) {
        player.setSound(context);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stop() {
        isPlaying = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        isPlaying = true;
        while(isPlaying) {
            player.play();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
