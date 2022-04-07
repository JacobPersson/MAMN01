package com.example.programmeringsuppgift;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.hardware.SensorEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.SensorEventListener;

public class Compass extends AppCompatActivity implements SensorEventListener {

    /** Filter variables. */
    private Sensor accSensor, magnetSensor;
    private int azimuth = 0;
    float[] lastAccelerometer = new float[3];
    float[] lastMagnetometer = new float[3];
    private boolean lastAccelerometerSet;
    private boolean lastMagnetometerSet;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private static final float FILTER_FACTOR = 0.25f;

    // device sensor manager
    private SensorManager sensorManager;

    // define the compass picture that will be use
    private ImageView compassImage;

    // record the angle turned of the compass picture
    private float DegreeStart = 0f;

    TextView DegreeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        // Compass methods
        compassImage = (ImageView) findViewById(R.id.compass_image);
        // TextView that will display the degree
        DegreeTV = (TextView) findViewById(R.id.DegreeTV);
        // initialize your android device sensor capabilities
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        /** Filter sensors. */
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // code for system's orientation sensor registered listeners
        /** sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME); */

        /** Filter. */
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        /** Normal case. */
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, sensorEvent.values);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        /** Deploying filters by filtering and saving arrays. */
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(filter(sensorEvent.values, lastMagnetometer), 0, lastMagnetometer, 0, sensorEvent.values.length);
            lastMagnetometerSet = true;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(filter(sensorEvent.values, lastAccelerometer), 0, lastAccelerometer, 0, sensorEvent.values.length);
            lastAccelerometerSet = true;
        }

        /** If the filters has been deployed, change the azimuth. */
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            azimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }
        azimuth = Math.round(azimuth); // change degree to azimuth.

        // get angle around the z-axis rotated
        //float degree = Math.round(sensorEvent.values[0]);
        DegreeTV.setText("Pekar mot " + Float.toString(azimuth) + " grader"); // degree

        // rotation animation - reverse turn degree degrees
        RotateAnimation ra = new RotateAnimation(
                DegreeStart,
                -azimuth, // degree
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        // set the compass animation after the end of the reservation status
        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(210);

        // Start animation of compass image
        compassImage.startAnimation(ra);
        DegreeStart = -azimuth; // degree

        /** Extension - Vibration */
        if (azimuth >= 345 || azimuth <= 15) { // degree
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(50);
                return;
            }
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
    public void onAccuracyChanged(Sensor sensor, int i) { }
}