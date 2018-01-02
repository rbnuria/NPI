package com.example.jlsuarezdiaz.museocajagranada;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToHome(View view){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
