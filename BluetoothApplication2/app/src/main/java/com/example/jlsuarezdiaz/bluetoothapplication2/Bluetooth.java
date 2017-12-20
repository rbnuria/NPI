package com.example.jlsuarezdiaz.bluetoothapplication2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Set;
import java.util.UUID;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by jlsuarezdiaz on 19/12/17.
 */

public class Bluetooth {
    private BluetoothAdapter BA;
    private BluetoothServerSocket BTserver_socket;

    private Activity activity;
    private BluetoothSocket BTsocket;

    /**
     * UUID. Connection identificator. The device will only accept connections with this UUID.
     */
    private static final long app_UUID_msb = 1232465454;
    private static final long app_UUID_lsb = 354653186;
    private static final UUID app_UUID = new UUID(app_UUID_msb, app_UUID_lsb);
    /**
     * Constructor.
     * Initializes Bluetooth Adapter.
     */
    public Bluetooth(Activity a){
        this.activity = a;
        BA = BluetoothAdapter.getDefaultAdapter();
        if(BA == null){
            System.err.println("El dispositivo no soporta Bluetooth.");
        }
        BTserver_socket = null;
        BTsocket = null;
    }

    /**
     * Enables Bluetooth.
     */
    public void turnOn(){
        int REQUEST_ENABLE_BT = 1;

        // Solicitamos la habilitación del bluetooth si no lo está ya
        if(!BA.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
        else{
            // Hacer algo (o no) si ya está habilitado.
        }
    }

    /**
     * Makes device visible.
     */
    public void getVisible(){
        int REQUEST_DISCOVERABLE_BT=2;

        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        activity.startActivityForResult(getVisible,REQUEST_DISCOVERABLE_BT);
    }

    /**
     * Disables Bluetooth.
     */
    public void turnOff(){
        BA.disable();
    }

    /**
     * Gets paired devices.
     */
    public void getBondedDevices(){

        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                System.out.println(device.getName()+"\t"+device.getAddress());
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            System.out.println("HE ENTRAO AQUI ILLO");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                System.out.println(device.getName()+"\t"+device.getAddress());
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };

    /**
     * Scans for devices.
     */
    public void scanDevices(){
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver,filter); // Don't forget to unregister during onDestroy
        BA.startDiscovery();
    }

    /**
     * Stops scan.
     */
    public void stopScan(){
        activity.unregisterReceiver(mReceiver);
        BA.cancelDiscovery();
    }

    /**
     * Starts listening for a BT connection.
     * WARNING: This function should not be called on the main thread.
     */
    public void startBluetoothServer() {
        try {
            //Escuchamos hasta recibir una petición.
            BTserver_socket = BA.listenUsingRfcommWithServiceRecord("NPI_BT", app_UUID);
            BTsocket = BTserver_socket.accept();
            //Cerramos el socket servidor tras haber recibido la petición.
            BTserver_socket.close();
            BTserver_socket = null;

        } catch (Exception ex) {
            System.err.println(ex.getStackTrace());
        }
    }

    /**
     * Starts a client BT connection..
     * WARNING: This function should not be called on the main thread.
     */
    public void clientBluetoothConnection(BluetoothDevice device){
        try{
            BTsocket = device.createRfcommSocketToServiceRecord(app_UUID);
            BTsocket.connect();
        }
        catch(Exception ex){
            System.err.println(ex.getStackTrace());
        }
    }
}
