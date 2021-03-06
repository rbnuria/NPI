package com.example.jlsuarezdiaz.museocajagranada;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GameStartActivity extends VoiceActivity
        implements GestureDetector.OnGestureListener, NavigationView.OnNavigationItemSelectedListener, GameModeFragment.OnFragmentInteractionListener,
        CountDownGameStartFragment.OnFragmentInteractionListener, Question1Fragment.OnFragmentInteractionListener, Question2Fragment.OnFragmentInteractionListener,
        Question3Fragment.OnFragmentInteractionListener, PointCounterFragment.OnFragmentInteractionListener{

    // Definiciones para la conexión bluetooth.
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //Variables que controlan la pregunta actual y el número de preguntas correctas
    public static int question = 0;
    public static int correct_answers = 0;

    private static final String LOGTAG = "GAMESTART";

    //Variable necesaria para el uso de sensores
    private SensorManager sensorManager;

    //Referencia al detector de gestos
    private GestureDetectorCompat gesturedetector = null;


    //Constantes para el reconocimiento de gestos (Stackoverflow)
    private static final int SWIPE_MIN_DISTANCE = 420;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    //Variables para establecer límites de movimiento y proximidad (gestos)
    private int limite_movimiento = 2;
    private double limite_proximidad = 0.01;

    //Variables para controlar el comportamiento del giroscopio.
    boolean right = false;
    boolean left = false;


    //Adaptador para la implementación de NFC
    public static NfcAdapter mNfcAdapter = null;

    //Respuesta dada por el usuario para cada una de las preguntas tipo test
    public static String question1_response, question2_response;

    //Booleano NFC Tag (juego)
    public static boolean NFC_activated = false;

    //Listener de eventos para el sensor
    private SensorEventListener mySensorEventListener;


    public static boolean isOnMap = false;

    /**
     * Creación de la actividad.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Para el uso del navigationView
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Uso de navigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Código de lanzamiento de un fragment (cuenta atrás para el inicio del juego).
        Fragment newFragment = new GameModeFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

        transaction.replace(R.id.fragment_placeholder,newFragment);
        transaction.addToBackStack(null);

        transaction.commit();

        //Servicio del dectector de gestos
        gesturedetector = new GestureDetectorCompat(this, this);


        //////////////// FUNCIONALIDAD DE LOS SENSORES

        //Obtenemos el servicio de sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Definimos la funcionalidad de los sensores
        mySensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                //GIROSCOPIO
                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    //Las variables rigth y left nos indican si no son el primer movimiento
                    //right nos indica si este primer movimiento fue hacia la derecha
                    //left de forma análoga para la izquierda

                    //En caso de ser el primer movimiento
                    if(!right && !left){
                        //Si ha superado el movimiento definido
                        if (sensorEvent.values[2] <= - limite_movimiento) {
                            right = true;

                            //Definimos una hebra auxiliar que se ejecutará en 0.5s.
                            //Su funcionalidad será volver a poner la variable rigth a false
                            //si al pasar el tiempo no se ha realizado el movimiento complementario.
                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    right = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }


                        //Procedimiento análogo para la izquierda
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            left = true;

                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    left = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }

                        //Si el primer movimiento había sido a la derecha
                    }else if(right) {
                        //Y ahora realizamos un movimiento hacia la izquierda (antes de 0.5s)
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            //Aumentamos pregunta actual en 1 y cambiamos al siguiente fragment (siguiente pregunta)
                            question = question + 1;
                            right = false;
                            onFragmentInteraction();
                        }

                        //Si el primer movimiento había sido a la izquierda
                    }else if(left){
                        //Y ahora realizamos un movimiento hacia la derecha (antes de 0.5s)
                        if (sensorEvent.values[2] <= - limite_movimiento) {
                            left = false;
                            //Disminuímos pregunta actual en 1 y cambiamos al siguiente fragment (pregunta anterior)
                            question = question - 1;
                            onFragmentInteraction();
                        }
                    }

                    // SENSOR DE PROXIMIDAD
                }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY ) {
                    if(question >= 1 && question <= 2) {
                        /// Si algo está lo suficientemente cerca (en nuestro caso será la mano)
                        if (sensorEvent.values[0] >= -limite_proximidad && sensorEvent.values[0] <= limite_proximidad) {
                            //Llamamos al método que nos maneja el método por voz
                            indicateListening();
                            startListening();
                        }
                    }
                }


            }

            //Necesario implementarlo por ser clase abstracta
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}


        };


        //Servicio del dectector de gestos
        gesturedetector = new GestureDetectorCompat(this, this);


        //Inicialización adaptador NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Floating Buttoon
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_game);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnMap = true;
                Intent intent = new Intent(GameStartActivity.this , MapActivity.class);
                startActivity(intent);
            }
        });

        initSpeechInputOutput(this);


    }

    /**
     * Reanudación de la actividad.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Registramos el listener para los 2 sensores
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    /**
     * Pausa de la actividad.
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Deshacemos registro del listener para el sensor
        sensorManager.unregisterListener(mySensorEventListener);
    }

    public boolean onTouch(View v, MotionEvent event){
        return true;
    }


    /**
     * Evento 'Pulsar atrás'.
     */
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // Si la pregunta es mayor que 1, disminuímos en 1 el número de pregunta actual y cambiamos a esa pregunta.
            if(question > 1 && question < 4) {
                question = question - 1;
                onFragmentInteraction();
            }else{
                //Si no estamos en las preguntas si no en el inicio, volvemos a HomeActivity
                Intent intent = new Intent(GameStartActivity.this,HomeActivity.class);
                startActivity(intent);
            }


        }
    }

    /**
     * Configuración de menús.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(GameStartActivity.this,HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_game) {
            Intent intent = new Intent(GameStartActivity.this, GameStartActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //Método que lanza el fragment de CountDown antes de comenzar el juego
    public void startGame(View view) {
        Fragment newFragment = new CountDownGameStartFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

        transaction.replace(R.id.fragment_placeholder,newFragment);

        //Ponemos el número de pregunta actual a 1
        question = 1;

        transaction.commit();
    }

    public void playMultiplayer(View view, boolean start) {
        //Intent serverIntent = new Intent(this, DeviceListActivity.class);
        //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        Intent multiplayerIntent = new Intent(this,MultiplayerStartActivity.class);
        System.out.println(start);
        multiplayerIntent.putExtra("START",start);
        startActivity(multiplayerIntent);
    }

    /*
     *  Función que controla el modo de juego (individual o multiplayer) elegido al iniciarse el juego.
     */
    @Override
    public void onFragmentInteraction(String text, View v) {
        System.out.println(text);
        switch(text){
            case "INDIVIDUAL":
                startGame(v);
                break;
            case "MULTIPLAYER":
                playMultiplayer(v,false);
                break;
            case "MULTIPLAYER_ST":
                playMultiplayer(v,true);
                break;
        }
    }


    /*
        Método que controla el cambio entre fragments que representan a cada una de las 3 preguntas del juego
        y la pantalla con la puntuación final.

        La funcionalidad depende del valor de la variable question en el momento de la llamada.
     */
    @Override
    public void onFragmentInteraction() {

        NFC_activated = false;

        //Definimos según el valor de la pregunta actual el cambio de fragments
        switch (question){
            //Pregunta 1
            case 1: {
                Fragment newFragment = new Question1Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder, newFragment);

                transaction.commit();

                break;
            }
            //Pregunta 2
            case 2:{
                Fragment newFragment = new Question2Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);

                transaction.commit();

                break;
            }

            //Pregunta 3
            case 3:{
                NFC_activated = true;
                Fragment newFragment = new Question3Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);

                transaction.commit();

                break;
            }

            //Fin del juego
            case 4:{
                Fragment newFragment = new PointCounterFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);

                transaction.commit();

                break;
            }
        }

    }


    //Para poder extender la clase abstracta
    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }


    /*
        Función basada en un código de Stackoverflow.

        Función que implementa la funcionalidad que de los gestos en pantalla.
        Implementamos swipe en las dos direcciones (derecha - izquierda / izquierda - derecha)
        utilizándolos respectivamente para pasar a la siguiente pregunta y volver a la anterior.

        Para ello utilizamos variables que representan los rangos mínimos para considerar que se ha producido el movimiento.
        Controlando una distancia recorrida mínima y una velocidad mínima al recorrerla.
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        try {
            if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // Derecha a izquierda
            if (motionEvent.getX() - motionEvent1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
                //Pasamos a la siguiente pregunta
                question = question +1;
                onFragmentInteraction();

                // Izquierda a derecha
            } else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
                //Pregunta anterior
                question = question - 1;
                onFragmentInteraction();

            }

        } catch (Exception e) {}
        return false;
    }


    //Interacción con los fragmentos tras un evento de movimiento.
    public void onFragmentInteraction(MotionEvent event) {
        this.gesturedetector.onTouchEvent(event);
    }



    /*
        Función utilizada para implementar la funcionalidad de las tarjetas NFC.

        Cuando se encuentra un nuevo "intent" cuya acción sea TAG_DISCOVERED (que se ha acercado una tarjeta al sensor)
        En ese momento, si la NFC obtenida tiene ID, diferenciamos dos casos en función del valor del booleano NFC_activated
        para diferenciar entre las dos funcionalidades para las que usamos las tarjetas.

        1. NFC_activated == true -> Para conseguir puntos de una pregunta de localización de objetos

            Para ello aumentamos en 1 el número de la pregunta actual, y el número de preguntas correctas hasta el momento
            y llamamos siguiente fragment

        2. NFC_activated == false -> Para decir dónde estás a la hora de mostrar el mapa con tu localización

            Comprueba en cuál de las 3 posiciones te encuentras, manda la imagen en cuestión a la activity
            MapActivity y te la muestra.

        Para comprobar en ambos casos que la tarjeta NFC es la requerida, obtenemos el ID (hexadecimal), lo convertimos
        a decimal con una función auxiliar y comprobamos si es el ID que tenemos almacenado en los Strings.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        if(intent.getAction() != null) {
            if (intent.getAction().equals(mNfcAdapter.ACTION_TAG_DISCOVERED)) {
                if (NFC_activated == true && intent.hasExtra(mNfcAdapter.EXTRA_ID)) {
                    byte[] tagId = intent.getByteArrayExtra(mNfcAdapter.EXTRA_ID);
                    String texto = ByteArrayToHexString(tagId);

                    if (texto.equals(getResources().getString(R.string.tag_nuria))) {
                        //Comprobamos que sea la que queremos (esto puede ser controlado con un booleano)
                        question = question + 1;
                        correct_answers = correct_answers + 1;
                        NFC_activated = false;
                        onFragmentInteraction();
                    }
                } else {

                    if (intent.hasExtra(mNfcAdapter.EXTRA_ID) && isOnMap) {

                        byte[] tagId = intent.getByteArrayExtra(mNfcAdapter.EXTRA_ID);
                        String texto = ByteArrayToHexString(tagId);
                        int imagen = 0;

                        if (texto.equals(getResources().getString(R.string.tag_nuria))) {
                            imagen = 1;
                        } else if (texto.equals(getResources().getString(R.string.tag_moya))) {
                            imagen = 2;
                        } else if (texto.equals(getResources().getString(R.string.tag_juanlu))) {
                            imagen = 3;
                        }

                        if (imagen > 0) {
                            Intent intent2 = new Intent(GameStartActivity.this, MapActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("imagen", imagen);
                            intent2.putExtras(bundle);
                            startActivity(intent2);
                        }
                    }

                }
            }
        }
    }


    /*
        Función obtenida en StackOverflow para convertir de hexadecimal a entero para convertir las IDs de las tarjetas
     */
    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";
        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    /* ---------- LISTENING METHODS ---------------- */

    // Constantes y variables para la escucha.
    private static Integer ID_PROMPT_QUERY = 0;
    private static Integer ID_PROMPT_INFO = 1;
    private long startListeningTime = 0;
    //Código de idioma.
    private String lang = "ES";

    //Cambios en la interfaz de usuario al iniciar escucha.
    private void indicateListening(){

    }

    // Comprobación de conexión a internet.
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    // Función que comienza a escuchar la voz del usuario.
    private void startListening(){

        if(deviceConnectedToInternet()){
            try {

				/*Start listening, with the following default parameters:
					* Recognition model = Free form,
					* Number of results = 1 (we will use the best result to perform the search)
					*/
                startListeningTime = System.currentTimeMillis();
                listen(Locale.ENGLISH, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, 1); //Start listening
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOGTAG, e.getMessage());
            }
        } else {
            Log.e(LOGTAG, "Device not connected to Internet");
        }
    }

    // Mensaje informativo para pedir permiso para utilizar el micrófono.
    @Override
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), "Usando el sensor de proximidad puedes hablar para elegir la respuesta. La aplicación necesita acceso al micrófono.", Toast.LENGTH_SHORT).show();
    }

    // Mensaje de rechazo si no se dan permisos de micrófono.
    @Override
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Lo sentimos. Para usar esta funcionalidad debes utilizar el micrófono.", Toast.LENGTH_SHORT).show();
    }

    // Procesamiento de los resultados escuchados.
    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {
        if (nBestList != null) {
            if (nBestList.size() > 0) {
                boolean matched = false;
                String userQuery = nBestList.get(0).toLowerCase(); //We will use the best result
                String[] back_keys = getResources().getStringArray(R.array.question_back_key);

                for(String s : back_keys){
                    System.out.println(s+" || "+userQuery);
                    if(userQuery.contains(s.toLowerCase())){
                        // Volver a la pregunta anterior.
                        Toast.makeText(getApplicationContext(), "Has vuelto a la pregunta anterior.", Toast.LENGTH_SHORT).show();

                        if(question > 1){
                            question = question - 1;
                        }

                        onFragmentInteraction();

                        matched = true;
                    }
                }

                String[] next_keys = getResources().getStringArray(R.array.question_next_key);
                for(String s : next_keys){
                    System.out.println(s.toLowerCase()+" || "+userQuery);
                    if(userQuery.contains(s.toLowerCase())){
                        System.out.println("OK");
                        // Pasar de pregunta.
                        Toast.makeText(getApplicationContext(), "Has pasado de pregunta.", Toast.LENGTH_SHORT).show();

                        question = question + 1;
                        onFragmentInteraction();

                        matched = true;
                    }
                }
                // Contestación de preguntas. Detectamos si se ha dicho alguna palabra clave por el usuario que identifique la respuesta.
                if(question == 1 || question == 2) {
                    for(int i = 1; i <= 3; i++) {
                        String keyname = "question"+Integer.toString(question)+Integer.toString(i)+"_keys";
                        System.out.println(keyname);
                        String[] question_keys = getResources().getStringArray(getResources().getIdentifier(keyname,"array",this.getPackageName()));
                        for(String s : question_keys){
                            System.out.println(s.toLowerCase()+" || "+userQuery);
                            if(userQuery.contains(s.toLowerCase())){
                                // Seleccionar opción `i` en el fragment `question`
                                Toast.makeText(getApplicationContext(), "Has seleccionado la opción "+Integer.toString(i), Toast.LENGTH_SHORT).show();

                                if(question == 1){
                                    GameStartActivity.question1_response = Integer.toString(i);
                                }else{
                                    GameStartActivity.question2_response = Integer.toString(i);
                                }

                                question = question + 1;
                                onFragmentInteraction();

                                matched = true;

                                break;
                            }

                        }

                        if (matched) {
                            break;
                        }

                    }
                }
                // Respuesta desconocida.
                if(!matched) {
                    Toast.makeText(getApplicationContext(), "No he entendido la respuesta.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void processAsrReadyForSpeech() {

    }

    //Manejo de errores en la escucha.
    @Override
    public void processAsrError(int errorCode) {
        //changeButtonAppearanceToDefault();

        //Possible bug in Android SpeechRecognizer: NO_MATCH errors even before the the ASR
        // has even tried to recognized. We have adopted the solution proposed in:
        // http://stackoverflow.com/questions/31071650/speechrecognizer-throws-onerror-on-the-first-listening
        long duration = System.currentTimeMillis() - startListeningTime;
        if (duration < 500 && errorCode == SpeechRecognizer.ERROR_NO_MATCH) {
            Log.e(LOGTAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. Going to ignore the error");
            stopListening();
        }
        else {
            String errorMsg = "";
            switch (errorCode) {
                case SpeechRecognizer.ERROR_AUDIO:
                    errorMsg = "Error grabando audio";//"Audio recording error";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    errorMsg = "Error desconocido en el lado del cliente";//"Unknown client side error";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    errorMsg = "Permisos insuficientes";//"Insufficient permissions";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    errorMsg = "Error de red.";//"Network related error";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    errorMsg = "Tiempo de conexión agotado.";//"Network operation timed out";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    errorMsg = "No hay coincidencias ";//"No recognition result matched";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    errorMsg = "Servicio de reconocimiento ocupado";//"RecognitionService busy";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    errorMsg = "Error del sevidor";//"Server sends error status";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    errorMsg = "No se detectó ninguna entrada";//"No speech input";
                    break;
                default:
                    errorMsg = ""; //Another frequent error that is not really due to the ASR, we will ignore it
            }
            if (errorMsg != "") {
                Log.e(LOGTAG, "Error when attempting to listen: " + errorMsg);
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                try {
                    //speak(errorMsg, lang, ID_PROMPT_INFO);
                } catch (Exception e) {
                    Log.e(LOGTAG, "English not available for TTS, default language used instead");
                }
            }
        }


    }

    @Override
    public void onTTSDone(String uttId) {
        if(uttId.equals(ID_PROMPT_QUERY.toString()))
            startListening();
    }

    @Override
    public void onTTSError(String uttId) {
        Log.e(LOGTAG, "TTS error");
    }

    @Override
    public void onTTSStart(String uttId) {
        Log.e(LOGTAG, "TTS starts speaking");
    }
}
