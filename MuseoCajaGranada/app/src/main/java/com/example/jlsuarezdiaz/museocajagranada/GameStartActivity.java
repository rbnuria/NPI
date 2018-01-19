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
import android.nfc.Tag;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GameStartActivity extends VoiceActivity
        implements GestureDetector.OnGestureListener, NavigationView.OnNavigationItemSelectedListener, GameModeFragment.OnFragmentInteractionListener,
        CountDownGameStartFragment.OnFragmentInteractionListener, Question1Fragment.OnFragmentInteractionListener, Question2Fragment.OnFragmentInteractionListener,
        Question3Fragment.OnFragmentInteractionListener, PointCounterFragment.OnFragmentInteractionListener{

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public static int question = 1;
    public static int correct_answers = 0;

    private static final String LOGTAG = "GAMESTART";

    //NFC TAGS
    private NfcAdapter mNfcAdapter;

    //Variables necesarias para el uso de los sensores
    private SensorManager sensorManager;

    //Referencia al detector de gestos
    private GestureDetectorCompat gesturedetector = null;
    private ScaleGestureDetector sgd;

    //CONSTANTES DEL RECONOCIMIENTO DE GESTO -> Stackoverflow
    private static final int SWIPE_MIN_DISTANCE = 420;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private int limite_movimiento = 2;
    private double limite_proximidad = 0.01;

    //Variables para controlar el comportamiento del giroscopio.
    boolean right = false;
    boolean left = false;

    // Vamos almacenando las respuestas introducidas para contabilizar al final
    public static String question1_response, question2_response;

    //Booleano NFCTags
    public static boolean NFC_activated = false;

    private SensorEventListener mySensorEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

                //////////////////////// GIROSCOPIO
                if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    //right nos indicará si el primer movimiento ha sido a la derecha
                    //left nos indicará lo mismo pero a la izquierda
                    //Si aun no hemos hecho un primer movimiento
                    if(!right && !left){
                        if (sensorEvent.values[2] <= - limite_movimiento) { // Podemos establecer límite en una v
                            right = true;
                            //hebra que se ejecutará en 0.5s. Esta hebra
                            //hace que si en 0.5s no hemos hecho el movimiento
                            //hacia la izquierda, right volverá a ser false

                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    right = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }


                        //Mismo procedimiento para la izquierda
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            left = true;
                            //hebra que se ejecutará en 0.5s. Esta hebra
                            //hace que si en 0.5s no hemos hecho el movimiento
                            //hacia la izquierda, right volverá a ser false

                            Runnable isElapsed = new Runnable() {
                                @Override
                                public void run() {
                                    left = false;
                                }
                            };

                            Handler h = new Handler();
                            h.postDelayed(isElapsed, 500);
                        }

                    }else if(right) { //Si habíamos ido a la derecha
                        //Y ahora vamos a la izquierda
                        if (sensorEvent.values[2] >= limite_movimiento) {
                            //A la derecha
                            question = question + 1;
                            right = false;
                            onFragmentInteraction();
                        }
                    }else if(left){ //Si habíamos ido a la izquierda
                        //Y ahora vamos a la derecha
                        if (sensorEvent.values[2] <= - limite_movimiento) {
                            // A la izquierda -> se podría volver a la anterior (permitir?)
                            left = false;
                            question = question - 1;
                            onFragmentInteraction();
                        }
                    }

                    //////////// SENSOR DE PROXIMIDAD
                }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY ) {
                    if(question >= 1 && question <= 2) {
                        /// Si algo está lo suficientemente cerca (en nuestro caso será la mano) -> minusvalías
                        if (sensorEvent.values[0] >= -limite_proximidad && sensorEvent.values[0] <= limite_proximidad) {
                            //Tapao
                            //question = question + 1;
                            //onFragmentInteraction();
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


        ///    FUNCIONALIDAD NFCTags
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        Tag myTag = (Tag) getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        System.out.println(myTag);



        //FLoating Buttooon!!!!!!
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_game);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameStartActivity.this , MapActivity.class);
                startActivity(intent);
            }
        });

        initSpeechInputOutput(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Registramos el listener para los 2 sensores
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(mySensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(mySensorEventListener);
    }

    public boolean onTouch(View v, MotionEvent event){
        return true;
    }


    @Override
    public void onBackPressed() {
        if(question > 1){
            question = question - 1;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            /*if (getFragmentManager().getBackStackEntryCount() > 0 ){
                getFragmentManager().popBackStack();
                }else{
                    super.onBackPressed();
                }
            }*/
            onFragmentInteraction();
        }
    }

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public void startGame(View view) {
        Fragment newFragment = new CountDownGameStartFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

        transaction.replace(R.id.fragment_placeholder,newFragment);
        //transaction.addToBackStack(null);

        transaction.commit();
    }

    public void playMultiplayer(View view) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public void onFragmentInteraction(String text, View v) {
        switch(text){
            case "INDIVIDUAL":
                startGame(v);
                break;
            case "MULTIPLAYER":
                playMultiplayer(v);
                break;
        }
    }


    @Override
    public void onFragmentInteraction() {

        //Siguiente pregunta
        NFC_activated = false;

        switch (question){
            case 1: {
                Fragment newFragment = new Question1Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder, newFragment);
                //transaction.addToBackStack(null);

                transaction.commit();

                break;
            }
            case 2:{
                Fragment newFragment = new Question2Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);
                //transaction.addToBackStack(null);

                transaction.commit();

                break;
            }

            case 3:{
                NFC_activated = true;
                Fragment newFragment = new Question3Fragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);
                //transaction.addToBackStack(null);

                transaction.commit();

                break;
            }

            case 4:{
                Fragment newFragment = new PointCounterFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.fade_in,R.animator.fade_out);

                transaction.replace(R.id.fragment_placeholder,newFragment);
                //transaction.addToBackStack(null);

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


    //Cuando deslizamos el dedo por la pantalla (derecha e izquierda) -> StackOverflow
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        try {
            if (Math.abs(motionEvent.getY() - motionEvent1.getY()) > SWIPE_MAX_OFF_PATH)
                return false;
            // right to left swipe
            if (motionEvent.getX() - motionEvent1.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
                question = question +1;
                System.out.println("HOLA!");
                onFragmentInteraction();

            } else if (motionEvent1.getX() - motionEvent.getX() > SWIPE_MIN_DISTANCE
                    && Math.abs(v) > SWIPE_THRESHOLD_VELOCITY) {
                // Igual -> se podría echar para atrás
                question = question - 1;
                onFragmentInteraction();

            }

        } catch (Exception e) {}
        return false;
    }



    public void onFragmentInteraction(MotionEvent event) {
        this.gesturedetector.onTouchEvent(event);
//        // Be sure to call the superclass implementation
//        return super.onTouchEvent(event);
    }


    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );

        if(NFC_activated == true && intent.hasExtra(mNfcAdapter.EXTRA_ID)){
            byte[] tagId = intent.getByteArrayExtra( mNfcAdapter.EXTRA_ID );
            String texto =  ByteArrayToHexString( tagId );

            if(texto.equals(getResources().getString(R.string.tag_nuria))){
                //Comprobamos que sea la que queremos (esto puede ser controlado con un booleano)
                question = question + 1;
                correct_answers = correct_answers + 1;
                onFragmentInteraction();
            }




        }
    }


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
    private static Integer ID_PROMPT_QUERY = 0;	//Id chosen to identify the prompts that involve posing questions to the user
    private static Integer ID_PROMPT_INFO = 1;	//Id chosen to identify the prompts that involve only informing the user
    private long startListeningTime = 0; // To skip errors (see processAsrError method)

    private String lang = "ES";

    private void indicateListening(){

    }
    public boolean deviceConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

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

    @Override
    public void showRecordPermissionExplanation() {
        Toast.makeText(getApplicationContext(), "Usando el sensor de proximidad puedes hablar para elegir la respuesta. La aplicación necesita acceso al micrófono.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordAudioPermissionDenied() {
        Toast.makeText(getApplicationContext(), "Lo sentimos. Para usar esta funcionalidad debes utilizar el micrófono.", Toast.LENGTH_SHORT).show();
    }

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
                        matched = true;
                    }
                }

                String[] next_keys = getResources().getStringArray(R.array.question_next_key);
                for(String s : next_keys){
                    System.out.println(s.toLowerCase()+" || "+userQuery);
                    if(userQuery.contains(s.toLowerCase())){
                        System.out.println("OK");
                        // Volver a la pregunta anterior.
                        Toast.makeText(getApplicationContext(), "Has pasado de pregunta.", Toast.LENGTH_SHORT).show();
                        matched = true;
                    }
                }

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
                                matched = true;
                            }
                        }
                    }
                }

                //changeButtonAppearanceToDefault();
                if(!matched) {
                    Toast.makeText(getApplicationContext(), "No he entendido la respuesta.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void processAsrReadyForSpeech() {

    }

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
