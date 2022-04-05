package com.example.programmeringsuppgift;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class Accelerator extends AppCompatActivity implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor accelerometer;

    TextView x, y, z, dir;
    private String[] direction;

    public Accelerator() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        direction = new String[]{null, null};
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerator);

        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        dir = findViewById(R.id.dir);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /** Get values and set new text. */
        float valueX = sensorEvent.values[0];
        float valueY = sensorEvent.values[1];
        float valueZ = sensorEvent.values[2];
        this.x.setText("X: " + Float.toString(valueX));
        this.y.setText("Y: " + Float.toString(valueY));
        this.z.setText("Z: " + Float.toString(valueZ));

        /** Handle direction. */
        if(valueX < -0.5) {
            direction[0] = "RIGHT";
        } else if (valueX > 0.5) {
            direction[0] = "LEFT";
        } else {
            direction[0] = null;
        }

        // Gives direction depending on y-axis value
        if(valueY < -0.5) {
            direction[1] = "DOWN";
        } else if (valueY > 0.5) {
            direction[1] = "UP";
        } else {
            direction[1] = null;
        }

        /** Set directions and change color of background. */
        if(direction[0] != null || direction[1] != null) {
            if(direction[0] != null && direction[1] != null) {
                dir.setText(direction[0] + " + " + direction[1]);
            } else if (direction[0] != null) {
                dir.setText(direction[0]);
            } else if (direction[1] != null) {
                dir.setText(direction[1]);
            }

            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            dir.setText("FLAT");
            getWindow().getDecorView().setBackgroundColor(Color.GREEN);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}