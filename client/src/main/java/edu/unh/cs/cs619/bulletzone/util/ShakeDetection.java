package edu.unh.cs.cs619.bulletzone.util;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * This is a Singleton class that provides a single instance of itself
 * throughout the application. It is used to detect user shakes to fire bullets..
 *
 * To use this class, call init with a Runnable, which should contain whatever instructions
 * you would like it to execute. Also pass it the device's sensor manager. Init allows just 1
 * instance of the Singleton.
 *
 * Example usage:
 *   SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
 *         Runnable run = new Runnable() {
 *             @Override
 *             public void run() {
 *                 onButtonFire();
 *             }
 *         };
 *         ShakeDetection.init(sm, run);
 */
public class ShakeDetection  {

    private static final int SENSITIVITY = 14;

    private static ShakeDetection instance = null;



    /**
     * Private constructor for the ShakeDetection class. Initializes the sensor event
     * listener with the specified SensorManager and Runnable objects. Only can be called by init,
     * for Singelton purposes.
     *
     * @param sensorManager The SensorManager to use for detecting shakes. Passed by init.
     * @param runnable The Runnable to be executed when a shake is detected. Passed by init.
     */
    private ShakeDetection(SensorManager sensorManager, Runnable runnable) {

        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize the event listener
        SensorEventListener sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                if (sensorEvent == null) return;

                // Grab delta values to calculate shake
                float x_accel = sensorEvent.values[0];
                float y_accel = sensorEvent.values[1];
                float z_accel = sensorEvent.values[2];

                // If values exceed that of the set sensitivity, run the shake detect
                if (Math.abs(x_accel) + Math.abs(y_accel) + Math.abs(z_accel) > SENSITIVITY)
                    runnable.run();

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(sel, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    /**
     * Initializes the ShakeDetection singleton with the specified SensorManager
     * and Runnable objects. If the singleton has already been created, this method
     * does nothing.
     *
     * @param sm The SensorManager to use for detecting shakes.
     * @param runnable The Runnable to be executed when a shake is detected.
     */
    public static ShakeDetection init(SensorManager sm, Runnable  runnable) {
        if (instance == null) instance = new ShakeDetection(sm, runnable);
        return instance;
    }
}
