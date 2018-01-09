package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by nuria on 9/1/18.
 */

public class PointCounterFragment extends Fragment {

    private int total_score = 0;

    TextView score_text = null;

    public PointCounterFragment() {
        // Required empty public constructor
    }

    //Método que realiza el recuento de los puntos obtenidos
    private void Checkpoints(){
        String q1 = GameStartActivity.question1_response;
        String q2 = GameStartActivity.question2_response;

        int count = 0;

        // Si hemos acertado la pregunta
        if(q1.equals(getResources().getString(R.string.q1_response) )){
            count++;
        }

        //Así con el resto de pregunatas
        if(q2.equals(getResources().getString(R.string.q2_response) )){
            count++;
        }

        total_score = count;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Question1Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointCounterFragment newInstance(String param1, String param2) {
        PointCounterFragment fragment = new PointCounterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        return v;

    }

    // TODO: Rename method, update argument and hook method into UI event

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


}
