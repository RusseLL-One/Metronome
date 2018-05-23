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
    private Context context;
    private ImageView knobImageView;
    private Matrix matrix;
    private float degrees = 0;
    private TextView debugTextView;
    private SoundPool rotateClickPlayer;
    private int rotateClickId;
    private int bpm = 10;

    RotaryKnob(Context context, ImageView knobImageView) {
        this.context = context;
        this.knobImageView = knobImageView;
        matrix = new Matrix();

        setKnobImage();

        debugTextView = ((MainActivity)context).findViewById(R.id.textview);

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
                    int step = (int)(currentDegrees/10) - (int)(newDegrees/10);
                    if (Math.abs(step) >= 1) {
                        //Если шаг слишком большой (например, при переходе от 0 к 360), то уменьшаем его
                        if(Math.abs(step) >= 30) step = -((step>0?1:-1) * 36 - step);
                        //TODO: сделать нормальную блокировку поворота при маленьком bpm
                        if(bpm+step < 10) { debugTextView.setText(String.valueOf(currentDegrees) + "\n" + newDegrees + "\nstartDegrees = " + startDegrees + "\nstep = " + step);
                            return false;}
                        bpm+=step;


                        rotateClickPlayer.play(rotateClickId, 1, 1, 0, 0, 1);
                    }
                    debugTextView.setText(String.valueOf(currentDegrees) + "\n" + newDegrees + "\nbpm = " + bpm + "\nstartDegrees = " + startDegrees + "\nstep = " + step);
                    currentDegrees = newDegrees;
                    turn(currentDegrees -startDegrees);
                    setDegrees(currentDegrees);
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

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setKnobImageView(ImageView knobImageView) {
        this.knobImageView = knobImageView;
    }

    public void setDegrees(float degrees) {
        this.degrees = degrees;
    }

    public ImageView getKnobImageView() {

        return knobImageView;
    }

    public float getDegrees() {
        return degrees;
    }
}
