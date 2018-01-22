package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Clase MainActivity.
 * Actividad inicial de la aplicación.
 */
public class MainActivity extends AppCompatActivity{

    //NFC TAGS
    public static NfcAdapter mNfcAdapter;

    // Creación de la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Acceso a la actividad Home.
    public void goToHome(View view){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
