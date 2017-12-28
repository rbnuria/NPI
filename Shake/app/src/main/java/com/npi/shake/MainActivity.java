package com.npi.shake;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements  GestureDetector.OnGestureListener{

    //Variables necesarias para el uso de los sensores
    private SensorManager sensorManager;

    //Referencia al detector de gestos
    private GestureDetectorCompat gesturedetector = null;

    TextView text_giroscope = null;
    TextView text_proximidad = null;
    TextView text_fling = null;

    //CONSTANTES DEL RECONOCIMIENTO DE GESTO -> Stackoverflow
    private static final int SWIPE_MIN_DISTANCE = 420;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private int limite_movimiento = 2;
    private double limite_proximidad = 0.01;

    //Variables para controlar el comportamiento del giroscopio.
    boolean right = false;
    boolean left = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Servicio del dectector de gestos
        gesturedetector = new GestureDetectorCompat(this, this);

        //////////////// FUNCIONALIDAD DE LOS SENSORES

        //Obtenemos el servicio de sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Definimos la funcionalidad de los sensores
        final SensorEventListener mySensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                text_giroscope = (TextView) findViewById(R.id.text_giroscope);

                //////////////////////// GIROSCOPIO
                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    //right nos indicará si el primer movimiento ha sido a la derecha
                    //left nos indicará lo mismo pero a la izquierda
                    //Si aun no hemos hecho un primer movimiento
                    if(!right && !left){
                        if (sensorEvent.values[2] <= - limite_movimiento) { // Podemos establecer límite en una v
                            right = true;
                            //hebra que se ejecutará en 0.5s. Esta hebra
                            //hace que si en 0.5s no hemos hecho el movimiento
                            //hacia la izquierda, right volverá a ser false

                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    right = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }


                        //Mismo procedimiento para la izquierda
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            left = true;
                            //hebra que se ejecutará en 0.5s. Esta hebra
                            //hace que si en 0.5s no hemos hecho el movimiento
                            //hacia la izquierda, right volverá a ser false

                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    left = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }

                    }else if(right) { //Si habíamos ido a la derecha
                        //Y ahora vamos a la izquierda
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            text_giroscope.setText("He girado a la derecha");
                            right = false;
                        }
                    }else if(left){ //Si habíamos ido a la izquierda
                        //Y ahora vamos a la derecha
                        if (sensorEvent.values[2] <= - limite_movimiento) {
                            text_giroscope.setText("He girado a la izquierda");
                            left = false;
                        }
                    }

                //////////// SENSOR DE PROXIMIDAD
                }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY ) {
                    text_proximidad = (TextView) findViewById(R.id.text_proximidad);

                    /// Si algo está lo suficientemente cerca (en nuestro caso será la mano) -> minusvalías
                    if (sensorEvent.values[0] >= -limite_proximidad && sensorEvent.values[0] <= limite_proximidad) {
                        text_proximidad.setText("Me han tapao");
                    }else{
                        text_proximidad.setText("Me han destapao");
                    }
                }


            }

            //Necesario implementarlo por ser clase abstracta
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}


        };


        //Registramos el listener para los 2 sensores
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    //Para poder extender la clase abstracta
    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }


    //Cuando deslizamos el dedo por la pantalla (derecha e izquierda) -> StackOverflow
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        text_fling = (TextView) findViewById(R.id.text_fling);

        Log.d("---onFling---", motionEvent.toString() + motionEvent1.toString() + "");

        try {
            if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if (motionEvent.getX() - motionEvent1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {

                    text_fling.setText("Han movido derecha -> izquierda");

            } else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {

                    text_fling.setText("Han movido izquierda -> derecha");
            }

        } catch (Exception e) {}
        return false;
    }

};
