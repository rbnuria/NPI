package com.example.jlsuarezdiaz.accelerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Shaker acc = new Shaker(this,2000);
        acc.startAccelerometer();
    }
}
