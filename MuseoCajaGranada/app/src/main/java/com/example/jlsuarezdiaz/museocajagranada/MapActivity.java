package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Clase MapActivity.
 * Representa un mapa orientable mediante los sensores del dispositivo.
 *
 * Clase basada en el ejemplo https://www.javacodegeeks.com/2013/09/android-compass-code-example.html.
 */
public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {



    // Imagen del mapa
    private ImageView image;

    // Ángulo de giro.
    private float currentDegree = 0f;

    // Sensor manager.
    private SensorManager mSensorManager;


    //Creación de la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Referencia a la imagen del mapa
        image = (ImageView) findViewById(R.id.map);

        //Inicializamos el sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Obtenemos los extras del intent con lo que hemos llamado, no será vacío cuando vengamos de GameStartActivity
        //pues contendrá el número de la imagen a mostrar

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //Si no es nulo
        if(extras != null){
            //Obtenemos la imagen
            int entero = extras.getInt( "imagen" );

            //En función de cual haya sido la elegida (mediante NFC tag) mostramos una imagen u otra
            if(entero == 1){
                image.setImageResource(R.drawable.artistas_andaluces);
            }else if(entero == 2){
                image.setImageResource(R.drawable.musulmanes);
            }else{
                image.setImageResource(R.drawable.sigo_xix);
            }
        }else{
            //Si no hay imagen elegida (es la general) mostramos mensaje de que puede decir en qué sala se encuentra
            //si se acerca a una NFC Tag.
            Toast.makeText(this, "Encuentre la NFC Tag de la sala para situarse.", Toast.LENGTH_LONG ).show();
        }

    }

    // Reanudación de la actividad
    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI);
        //mSensorManager.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_UI);
    }

    // Pausa de la actividad
    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    // Evento 'Pulsar atrás'
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Manejo de menús.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_game) {
            Intent intent = new Intent(this, GameStartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Manejo del giro del mapa. Detecta la orientación del dispositivo y gira el mapa dependiendo de esa orientación.
     * @param event Evento que ha producido el cambio en el sensor.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;


    }

    // Eventos no usados.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent( intent );


    }

}


