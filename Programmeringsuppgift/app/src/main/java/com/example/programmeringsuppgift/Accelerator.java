package com.example.programmeringsuppgift;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Accelerator extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    TextView x, y, z, directionText;
    private String[] direction = {null, null};
    private float[] accReadings = new float[3];
    private static final float FILTER_FACTOR = 0.5f;
    public static final int VERY_LIGHT_BLUE = Color.rgb(51,204,255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerator);

        /** Initialization. */
        x = findViewById(R.id.xAxis);
        y = findViewById(R.id.yAxis);
        z = findViewById(R.id.zAxis);
        directionText = findViewById(R.id.directionText);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accReadings = filter(event.values, accReadings);

        /** Get new values and set text on screen. */
        float xValue = Math.round(accReadings[0]);
        float yValue = Math.round(accReadings[1]);
        float zValue = Math.round(accReadings[2]);
        x.setText("X: " + Float.toString(xValue));
        y.setText("Y: " + Float.toString(yValue));
        z.setText("Z: " + Float.toString(zValue));

        /** Handle directions. */
        if (xValue < -0.5) {
            direction[0] = "RIGHT";
        } else if (xValue > 0.5) {
            direction[0] = "LEFT";
        } else {
            direction[0] = null;
        }

        if (yValue < -0.5) {
            direction[1] = "DOWN";
        } else if (yValue > 0.5) {
            direction[1] = "UP";
        } else {
            direction[1] = null;
        }

        /** Update directions and set background color. */
        if (direction[0] != null || direction[1] != null) {
            if (direction[0] != null && direction[1] != null) {
                directionText.setText(direction[0] + " + " + direction[1]);
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            } else if (direction[0] != null) {
                directionText.setText(direction[0]);
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            } else if (direction[1] != null) {
                directionText.setText(direction[1]);
                getWindow().getDecorView().setBackgroundColor(Color.GREEN);
            }
        } else {
            directionText.setText("FLAT");
            getWindow().getDecorView().setBackgroundColor(VERY_LIGHT_BLUE);
        }
    }

    private float[] filter(float[] input, float[] output) {
        if (output == null) {
            return input;
        }

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + FILTER_FACTOR * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}