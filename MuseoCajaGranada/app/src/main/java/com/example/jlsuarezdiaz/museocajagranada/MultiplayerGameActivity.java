package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import static com.example.jlsuarezdiaz.museocajagranada.MultiplayerStartActivity.MESSAGE_DEVICE_NAME;
import static com.example.jlsuarezdiaz.museocajagranada.MultiplayerStartActivity.MESSAGE_TOAST;
import static com.example.jlsuarezdiaz.museocajagranada.MultiplayerStartActivity.TOAST;
import static com.example.jlsuarezdiaz.museocajagranada.MultiplayerStartActivity.mBluetoothService;

/**
 * Clase MultiplayerGameActivity.
 * Actividad encargada de controlar el juego multijugador mediante conexión Bluetooth.
 */
public class MultiplayerGameActivity extends AppCompatActivity {
    //Mensajes para el juego.
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    // Comprobador de la finalización de partida.
    private volatile boolean partnerPressed = false;

    //Creación de la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game);
        mBluetoothService.setHandler(mHandler);
    }


    // Manejador para controlar la conexión entre los dispositivos.
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_STATE_CHANGE:
                    break;
                case MESSAGE_READ: // Recepción de mensajes.
                    byte[] read_buf = (byte[]) msg.obj;
                    String readMessage = new String(read_buf,0,msg.arg1);
                    String[] data = readMessage.split(":");
                    System.out.println(data[0]);
                    if(data.length >= 2) {
                        if (data[0].equals("1")) { // El compañero ha respondido a la pregunta 1. Actualizamos e informamos.
                            ((EditText)findViewById(R.id.q1Answer)).setText(data[1]);
                            Toast.makeText(getApplicationContext(),"Tu compañero ha respondido a la pregunta 1.",Toast.LENGTH_LONG).show();
                        } else if (data[0].equals("2")){ // El compañero ha respondido a la pregunta 2. Actualizamos e informamos.
                            ((EditText)findViewById(R.id.q2Answer)).setText(data[1]);
                            Toast.makeText(getApplicationContext(),"Tu compañero ha respondido a la pregunta 2.",Toast.LENGTH_LONG).show();
                        }
                        else if(data[0].equals("END")) { // Finalización de la partida. Si ambos lo han hecho, obtenemos puntuación.
                                                         // Mientras no hayan pulsado los dos solo se informa al compañero.
                            if (!partnerPressed) {
                                Toast.makeText(getApplicationContext(), "Tu compañero ha pulsado 'Finalizar'. Cuando lo pulséis los dos sabréis vuestra puntuación.", Toast.LENGTH_LONG).show();
                                partnerPressed = true;
                            }
                            else{
                                //Computar puntuaciones.
                                computePoints();
                            }

                        }
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_DEVICE_NAME:
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        }
    };

    public void onQ1Click(View v){ // Envío de respuesta a la pregunta 1 al compañero
        TextView view = (EditText) findViewById(R.id.q1Answer);
        String message = "1:"+view.getText().toString();
        sendMessage(message);
    }

    public void onQ2Click(View v){ // Envío de respuesta a la pregunta 2 al compañero.
        TextView view = (EditText) findViewById(R.id.q2Answer);
        String message = "2:"+view.getText().toString();
        sendMessage(message);
    }

    // Manejo de la finalización de la partida.
    public void onSubmitClick(View v){
        if(((Button)findViewById(R.id.btEnd)).getText().equals("FINALIZAR")) {
            //Mensaje de indicador de final.
            String message = "END:END";
            sendMessage(message);
            if(!partnerPressed){
                partnerPressed = true;
            }
            else{ // Si los dos han pulsado ya finalizar, obtenemos la puntuación.
                computePoints();
            }
        }
        else{ // Finalización de la actividad tras haber obtenido la puntuación.
            Intent goHomeIntent = new Intent(this,HomeActivity.class);
            startActivity(goHomeIntent);
        }
    }

    // Método de envío de mensajes.
    private void sendMessage(String message) {

        // Comprobación de conexión.
        if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "No estás conectado.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Comprobación de contenido en mensaje.
        if (message.length() > 0) {
            // Obtención del mensaje y envío al servicio Bluetooth.
            byte[] send = message.getBytes();
            mBluetoothService.write(send);

        }
    }

    // Obtención de puntuaciones.
    private void computePoints(){
        int points = 0;
        String ans1 = ((EditText)findViewById(R.id.q1Answer)).getText().toString();
        String ans2 = ((EditText)findViewById(R.id.q2Answer)).getText().toString();
        if(ans1.equals("711")){
            points++;
        }
        if(ans2.toLowerCase().equals("cádiz") || ans2.toLowerCase().equals("cadiz")){
            points++;
        }
        ((TextView)findViewById(R.id.textResult)).setText("¡HABÉIS CONSEGUIDO "+Integer.toString(points)+" PUNTOS!");
        ((Button)findViewById(R.id.btEnd)).setText("SALIR");
    }
}
