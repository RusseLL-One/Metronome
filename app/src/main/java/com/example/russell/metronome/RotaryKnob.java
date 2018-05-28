package com.example.russell.metronome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class RotaryKnob {
    private final float KNOB_WIDTH = 500;
    private final float KNOB_HEIGTH = 500;
    private final int MIN_BPM = 10;
    private final int MAX_BPM = 300;
    private Context context;
    private ImageView knobImageView;
    private Matrix matrix;
    private TextView debugTextView;
    private TextView bpmTextView;
    private SoundPool rotateClickPlayer;
    private int rotateClickId;
    private int bpm = 10;
    ClickPlayerTask clickPlayerTask;

    RotaryKnob(Context context) {
        this.context = context;
        this.knobImageView = ((MainActivity)context).findViewById(R.id.knobImageView);
        this.bpmTextView = ((MainActivity)context).findViewById(R.id.bpmTextView);
        matrix = new Matrix();

        setKnobImage();

        debugTextView = ((MainActivity)context).findViewById(R.id.debugTextView);

        rotateClickPlayer = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        rotateClickId = rotateClickPlayer.load(context, R.raw.click, 1);
    }

    private void setKnobImage() {
        Bitmap knobImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.knob);
        Matrix imageSizeMatrix = new Matrix();
        //TODO: Размер изображения задаётся в пикселях, нужно конвертировать в dp
        imageSizeMatrix.preScale(KNOB_WIDTH / knobImage.getWidth(), KNOB_HEIGTH / knobImage.getHeight());
        knobImageView.setImageBitmap(Bitmap.createBitmap(knobImage, 0, 0,
                knobImage.getWidth(),knobImage.getHeight() , imageSizeMatrix , true));
        knobImageView.setScaleType(ImageView.ScaleType.MATRIX);
        knobImageView.setImageMatrix(matrix);
        knobImageView.setOnTouchListener(knobRotateListener);
    }

    private void turn(float degrees) {
        matrix.setRotate(degrees, knobImageView.getWidth()/2, knobImageView.getHeight()/2);
        knobImageView.setImageMatrix(matrix);
    }

    private float cartesianToPolar(float x, float y) {
        return 180 + (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }

     private View.OnTouchListener knobRotateListener = new View.OnTouchListener() {
        float startDegrees;
        float currentDegrees;


        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float startX = event.getX() /
                                    ((float) knobImageView.getWidth());
                    float startY = event.getY() /
                                    ((float) knobImageView.getHeight());
                    currentDegrees = cartesianToPolar(startX, startY);
                    //Вычисляем начальные градусы с учётом предыдущего поворота
                    startDegrees = currentDegrees - startDegrees;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDegrees;
                    float x = event.getX() /
                                    ((float) knobImageView.getWidth());
                    float y = event.getY() /
                                    ((float) knobImageView.getHeight());
                    newDegrees = cartesianToPolar(x, y);
                    //bpm увеличивается каждые 10 градусов. 10 градусов - это наш шаг
                    int step = (int)(newDegrees/10) - (int)(currentDegrees/10);
                    if (Math.abs(step) >= 1) {
                        //Если шаг слишком большой (например, при переходе от 0 к 360), то уменьшаем его
                        if(Math.abs(step) >= 30) step = -((step>0?1:-1) * 36 - step);

                        if(bpm+step >= MIN_BPM && bpm+step <= MAX_BPM) {
                            bpm += step;
                            if(clickPlayerTask != null) {
                                clickPlayerTask.setBPM(bpm);
                            }
                        }

                        rotateClickPlayer.play(rotateClickId, 0.75f, 0.75f, 0, 0, 1);
                    }
                    bpmTextView.setText("BPM:\n" + bpm);
                    currentDegrees = newDegrees;
                    turn(currentDegrees -startDegrees);
                    break;
                case MotionEvent.ACTION_UP:
                    startDegrees = currentDegrees - startDegrees;
                    //Теперь startDegrees содержит градусы, на которые повёрнута ручка
                    v.performClick();
                    break;
            }
            return true;
        }
    };

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getBpm() {
        return bpm;
    }

    public void setPlayerTask(ClickPlayerTask task) {
        this.clickPlayerTask = task;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setKnobImageView(ImageView knobImageView) {
        this.knobImageView = knobImageView;
    }

    public ImageView getKnobImageView() {

        return knobImageView;
    }
}
