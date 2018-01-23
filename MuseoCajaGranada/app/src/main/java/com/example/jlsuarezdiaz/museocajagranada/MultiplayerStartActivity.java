package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Clase MultiplayerStartActivity.
 * Clase encargada de iniciar el modo de juego multijugador, estableciendo para ello
 * los mecanismos de comunicación por Bluetooth.
 */
public class MultiplayerStartActivity extends AppCompatActivity {
    // Tipos de mensaje
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Palabras clave
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Definiciones para la conexión bluetooth.
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Elementos bluetooth.
    private BluetoothAdapter mBluetoothAdapter = null;
    public static BluetoothChatService mBluetoothService = null;
    private StringBuffer mOutStringBuffer;

    //Creación de la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_start);

        // Obtención del modo de inicio de la conexión bluetooth (como cliente [START=false] o como servidor [START=true])
        Intent intent = getIntent();
        boolean start = intent.getExtras().getBoolean("START");
        System.out.println(start);
        if(!start){
            // Iniciación de la búsqueda de dispositivos si se inicia como cliente.
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        //Adaptador bluetooth.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Comprobación de soporte de Bluetooth.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    // Iniciación de actividad
    @Override
    protected void onStart() {
        super.onStart();
        //Habilitación de bluetooth si no se ha dado permiso aún.
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
        // Iniciación del servicio Bluetooth.
        else{
            if(mBluetoothService == null) setupService();
        }
    }

    // Reanudación de la actividad.
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothChatService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    //Iniciación del servicio Bluetooth.
    private void setupService(){
        mBluetoothService = new BluetoothChatService(this, mHandler);


        mOutStringBuffer = new StringBuffer("");
    }


    // Manejador para controlar la conexión entre los dispositivos.
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_DEVICE_NAME:   // Nombre del dispositivo. La conexión se ha establecido e iniciamos el juego.
                    Intent startGame = new Intent(getBaseContext(),MultiplayerGameActivity.class);
                    startActivity(startGame);
                    break;
                case MESSAGE_TOAST:  // Mensajes de error. Salimos del juego.
                    Toast.makeText(getApplicationContext(),msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };


     public void onActivityResult(int requestCode, int resultCode, Intent data){
         switch(requestCode){
             case REQUEST_CONNECT_DEVICE:  // Tras la selección de un dispositivo Bluetooth como cliente, intentamos conectar con él.
                 if(resultCode == Activity.RESULT_OK){
                     String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                     BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                     mBluetoothService.connect(device);
                 }
                 break;
             case REQUEST_ENABLE_BT:  // Comprobación de permisos Bluetooth.
                 if(resultCode == Activity.RESULT_OK){
                     setupService();
                 }
                 else{
                      Toast.makeText(this, "No se puede jugar a este modo sin activar el Bluetooth", Toast.LENGTH_SHORT).show();
                 }    finish();
         }
     }

     // Hacer Bluetooth visible para otros dispositivos.
     public void discoverable(View v){
         ensureDiscoverable();
     }

    private void ensureDiscoverable() {
         if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
             Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
             discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
             startActivity(discoverableIntent);
         }
    }
}
