package com.example.jlsuarezdiaz.accelerapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Created by jlsuarezdiaz on 20/12/17.
 */

public class Shaker extends AbstractAccelerometer {
    private final long SHAKE_THRESHOLD;

    public Shaker(Activity a){
        super(a);
        SHAKE_THRESHOLD = 600;
    }

    /**
     * Constructor
     * @param a Activity
     * @param shake_threshold Lowest value at which the device will detect a move as a shake.
     */
    public Shaker(Activity a, long shake_threshold){
        super(a);
        SHAKE_THRESHOLD = shake_threshold;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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


                if(x*100 > SHAKE_THRESHOLD){
                    System.out.println("DERECHA "+x*100);
                }
                if(x*100 < -SHAKE_THRESHOLD){
                    System.out.println("IZQUIERDA "+x*100);
                }
                if(y*100 > SHAKE_THRESHOLD){
                    System.out.println("ARRIBA "+y*100);
                }
                if(y*100 < -SHAKE_THRESHOLD){
                    System.out.println("ABAJO "+y*100);
                }
                if(z*100 > SHAKE_THRESHOLD){
                    System.out.println("PALANTE "+z*100);
                }
                if(z*100 < -SHAKE_THRESHOLD){
                    System.out.println("PATRÁS "+z*100);
                }

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
