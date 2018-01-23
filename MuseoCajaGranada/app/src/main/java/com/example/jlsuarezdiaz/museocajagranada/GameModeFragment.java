package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Clase GameModeFragment.
 * Fragmento para escoger el modo de juego.
 */
public class GameModeFragment extends Fragment {


    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;



    private OnFragmentInteractionListener mListener;

    /**
     * Constructor.
     */
    public GameModeFragment() {

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
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_game_mode, container, false);
        // Inflate the layout for this fragment
        final Button btStartIndividual = (Button) v.findViewById(R.id.btStartIndividual);
        btStartIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onFragmentInteraction("INDIVIDUAL",v);
                }
            }
        });
        final Button btMultiplayer = (Button) v.findViewById(R.id.btMultiplayer);
        btMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onFragmentInteraction("MULTIPLAYER",v);
                }
            }
        });
        final Button btMultiplayerSt = (Button) v.findViewById(R.id.btMultiplayerSt);
        btMultiplayerSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.onFragmentInteraction("MULTIPLAYER_ST",v);
                }
            }
        });
        return v;
    }

    /**
     * Asociación con actividad.
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
     * Desasociación.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Interfaz para la activación de eventos en la actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String text, View v);
    }


    /**
     * Activación de la actividad de selección de dispositivos Bluetooth.
     */
    public void playMultiplayer(View view) {
        Intent serverIntent = new Intent(this.getContext(), DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
}
