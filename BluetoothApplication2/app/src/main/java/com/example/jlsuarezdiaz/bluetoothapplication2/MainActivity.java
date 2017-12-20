package com.example.jlsuarezdiaz.bluetoothapplication2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Bluetooth BT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BT = new Bluetooth(this);
        BT.turnOn();
        BT.scanDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BT.stopScan();
    }
}
