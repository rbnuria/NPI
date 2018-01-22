package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import static java.lang.Thread.sleep;


/**
 * Fragmento que muestra una cuenta atrás para el comienzo del juego en GameStartActivity.
 */
public class CountDownGameStartFragment extends Fragment {
    /**
     * Listener para la interacción con actividades.
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Constructor por defecto.
     */
    public CountDownGameStartFragment() {
    }


    /**
     * Creación del fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Creación de la vista.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_count_down_game_start, container, false);
        final ProgressBar pb = (ProgressBar) v.findViewById(R.id.progressBarToday);
        final TextView tv = (TextView) v.findViewById(R.id.progressText);

        Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        an.setFillAfter(true);
        pb.startAnimation(an);
        final int maxTime = 3000;
        final int period = pb.getMax();
        pb.setProgress(maxTime);

        final int interval = 1;
        //Temporizador.
        new CountDownTimer(maxTime,interval){
            @Override
            public void onTick(long millisUntilFinished) {
                // Se cambia el progreso en la cuenta atrás.
                pb.setProgress((int)millisUntilFinished % period);
                tv.setText(Long.toString(millisUntilFinished/period+1));
            }

            @Override
            public void onFinish() {
                // Se activa la interacción con la activity al finalizar la cuenta atrás.
                pb.setProgress(0);
                tv.setText("¡YA!");

                mListener.onFragmentInteraction();
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }.start();
        // Inflate the layout for this fragment
        return v;
    }


    /**
     * Asociación de fragmento a actividad.
     */
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

    /**
     * Desasociación del fragmento.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interfaz de interacción, para implementar en las actividades que utilicen este fragmento.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
