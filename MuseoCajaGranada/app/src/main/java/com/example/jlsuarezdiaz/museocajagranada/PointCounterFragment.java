package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by nuria on 9/1/18.
 * Class PointCounterFragment.
 * Fragmento encargado de realizar recuento de puntos y finalizar el juego.
 */

public class PointCounterFragment extends Fragment {

    private int total_score = 0;

    TextView score_text = null;

    private OnFragmentInteractionListener mListener;

    public PointCounterFragment() {
        // Required empty public constructor
    }

    /*
     *  Método que realiza el recuento de los puntos obtenidos al finalizar el juego.
     */
    private void Checkpoints(){
        //Obtenemos variables de la activity del juego.
        String q1 = GameStartActivity.question1_response;
        String q2 = GameStartActivity.question2_response;

        int count = GameStartActivity.correct_answers;

        // Si hemos acertado la pregunta aumentamos en 1
        if(q1 != null && q1.equals(getResources().getString(R.string.q1_response) )){
            count++;
        }

        //Si hemos acertado la pregunta aumentamos en 1
        if(q2 != null && q2.equals(getResources().getString(R.string.q2_response) )){
            count++;
        }

        //Variable con el total de puntos conseguidos
        total_score = count;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Llamámos al método de recuento de puntos.
        Checkpoints();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.pointcounter_layout, container, false);
        score_text = (TextView) v.findViewById( R.id.score_tv );
        score_text.setText( "¡ENHORABUENA! Ha conseguido " + total_score + " puntos." );

        //Botón para salir del juego.
        final Button exitButton = (Button) v.findViewById(R.id.button_exit);

        //Definimos la funcionalidad del click en el botón
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Cuando realizamos click en el botón
                //Ponemos el número de pregunta a 0
                //Volvemos a HomeActivity (inicio de la app)
                if(mListener != null){
                    GameStartActivity.question = 0;
                    Intent intent = new Intent(getActivity(),HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

        return v;

    }

    /**
     * Interfaz de interacción con actividades.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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



}
