package com.example.russell.metronome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    //ImageView imageView;
    RotaryKnob knob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);

        //knob = new RotaryKnob(this, (ImageView)findViewById(R.id.knobImageView));

    }

    @Override
    protected void onResume() {
        knob = new RotaryKnob(this, (ImageView)findViewById(R.id.knobImageView));
        super.onResume();
    }
}
