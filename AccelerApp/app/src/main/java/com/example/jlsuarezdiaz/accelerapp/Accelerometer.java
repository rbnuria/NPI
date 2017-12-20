package com.example.jlsuarezdiaz.accelerapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by jlsuarezdiaz on 20/12/17.
 */

public class Accelerometer extends AbstractAccelerometer{
    Activity activity;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private long lastUpdate;
    private float lastX, lastY, lastZ;

    private static final int SHAKE_THRESHOLD = 600;

    Accelerometer(Activity a){
        super(a);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        /**
         * EJES:
         * +x: Móvil hacia su derecha.
         * -x: Móvil hacia su izquierda.
         * +y: Móvil hacia arriba.
         * -y: Móvil hacia abajo.
         * +z: Móvil hacia atrás.
         * -z: Móvil hacia adelante.
         */
        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long diffTime = updateIf(100);
            if (diffTime > 0) { //Si han pasado 100 ms desde el último update (para no usar constantemente el acelerómetro.
                // Obtenemos valores de la aceleración.
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float diffX = (x - lastX);///diffTime;
                float diffY = (y - lastY);///diffTime;
                float diffZ = (z - lastZ);///diffTime;
                System.out.println("X: " + diffX + "; Y: " + diffY + "; Z: " + diffZ);

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
