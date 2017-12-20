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

/**
 * Abstract class to manage any application based on accelerometer.
 */
public abstract class AbstractAccelerometer implements SensorEventListener {
    protected Activity activity;

    protected SensorManager sensorManager;
    protected Sensor accelerometer;

    protected long lastUpdate;
    protected float lastX, lastY, lastZ;


    AbstractAccelerometer(Activity a){
        this.activity = a;
        this.lastUpdate = 0;

        // Inicialización de sensores.
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void startAccelerometer(){
        // Registramos el sensor. La clase this recibirá los datos del acelerómetro en los ventos que implementa la interfaz (onXXXX),
        // con el delay especificado.
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopAccelerometer(){
        // El accelerómetro está actuando all el rato, es bueno pararlo si se cambia de actividad, si se pausa la app, etc.
        sensorManager.unregisterListener(this);
    }

    /**
     * Method used to manage accelerometer in a period of time instead of at every moment.
     * Handle accelerometer events only when this function returns true.
     * @param period Period of time the accelerometer events should be managed.
     * @return True, iff period has gone since last update.
     */
    protected long updateIf(long period){
        long curTime = System.currentTimeMillis();
        if((curTime - lastUpdate) > period){
            long difftime = (curTime - lastUpdate);
            lastUpdate = curTime;
            return difftime;
        }
        return 0;
    }

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    @Override
    public abstract void onAccuracyChanged(Sensor sensor, int accuracy);
}
