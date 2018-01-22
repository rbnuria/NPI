package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;


/**
 * Fragmento de la pregunta 1 del juego.
 */
public class Question1Fragment extends Fragment implements  GestureDetector.OnGestureListener{


    private RadioGroup radioGroup;

    public static String q1_response_user = "-1";

    //Explicado en GameStartActivity
    private static final int SWIPE_MIN_DISTANCE = 420;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;


    private OnFragmentInteractionListener mListener;

    //Referencia al detector de gestos
    private GestureDetectorCompat gesturedetector = null;
    private ScaleGestureDetector sgd;

    // Constructor
    public Question1Fragment() {

    }

    // Creación del fragmento
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Creación de la vista.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_question1, container, false);

        //RadioGroup utilizado para las opciones de las preguntas.
        radioGroup = (RadioGroup) v.findViewById(R.id.response_q1);

        //Para que reconozca el gesto
        v.setLongClickable(true);

        //Funcionalidad cuando pulsamos una de las opciones de radioGroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                //Almacenamos en una variable cual hemos seleccionado
                if(checkedId != -1){
                    if(checkedId == R.id.q1_response1) {
                        q1_response_user = "1";
                    } else if(checkedId == R.id.q1_response2) {
                        q1_response_user = "2";
                    } else if(checkedId == R.id.q1_response3) {
                        q1_response_user = "3";
                    }
                }

                //Asignamos el valor a la variable de GameStartActivity
                GameStartActivity.question1_response = q1_response_user;
            }
        });


        //Detector de gestos
        gesturedetector = new GestureDetectorCompat( getActivity(), this);

        //Listener del gesture detector
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesturedetector.onTouchEvent(event);
            }
        });

        return v;

    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * Interfaz de interacción con la actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    //Para poder extender la clase abstracta


    /*
        Función basada en un código de Stackoverflow.

        Función que implementa la funcionalidad que de los gestos en pantalla.
        Implementamos swipe en las dos direcciones (derecha - izquierda / izquierda - derecha)
        utilizándolos respectivamente para pasar a la siguiente pregunta y volver a la anterior.

        Para ello utilizamos variables que representan los rangos mínimos para considerar que se ha producido el movimiento.
        Controlando una distancia recorrida mínima y una velocidad mínima al recorrerla.
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        try {
            if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if (motionEvent.getX() - motionEvent1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {

                GameStartActivity.question = GameStartActivity.question + 1;
                ((GameStartActivity)getActivity()).onFragmentInteraction();
                //Llamar al de esta clase


            } else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
                // Igual -> se podría echar para atrás

                GameStartActivity.question = GameStartActivity.question - 1;
                ((GameStartActivity)getActivity()).onFragmentInteraction();

            }

        } catch (Exception e) {}
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }


}
