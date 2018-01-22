package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/**
 * Fragmento para la pregunta 2 del juego.
 */
public class Question2Fragment extends Fragment  implements  GestureDetector.OnGestureListener{

    //Explicado en GameStartActivity
    private static final int SWIPE_MIN_DISTANCE = 420;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private RadioGroup radioGroup;

    public static String q2_response_user = "-1";

    private OnFragmentInteractionListener mListener;

    //Referencia al detector de gestos
    private GestureDetectorCompat gesturedetector = null;
    private ScaleGestureDetector sgd;

    public Question2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_question2, container, false);

        //RadioGroup utilizado para las opciones de las preguntas.
        radioGroup = (RadioGroup) v.findViewById(R.id.response_q2);

        //Funcionalidad cuando pulsamos una de las opciones de radioGroup
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = v.findViewById( checkedId );
                rb.setChecked(true);

                //Almacenamos en una variable cual hemos seleccionado
                if(checkedId != -1){
                    if(checkedId == R.id.q2_response1) {
                        q2_response_user = "1";
                    } else if(checkedId == R.id.q2_response2) {
                        q2_response_user = "2";
                    } else if(checkedId == R.id.q2_response3) {
                        q2_response_user = "3";
                    }

                    //Asignamos el valor a la variable de GameStartActivity
                    GameStartActivity.question2_response = q2_response_user;
                }

            }
        });

        //Para que reconozca el gesto
        v.setLongClickable(true);

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
