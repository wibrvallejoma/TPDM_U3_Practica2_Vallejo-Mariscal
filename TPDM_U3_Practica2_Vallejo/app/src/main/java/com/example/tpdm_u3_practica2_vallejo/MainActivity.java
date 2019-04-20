package com.example.tpdm_u3_practica2_vallejo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    Button autos, motos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autos = findViewById(R.id.btnAutos);
        motos = findViewById(R.id.btnMotos);

        autos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAutos = new Intent(MainActivity.this, AutosActivity.class);
                startActivity(intentAutos);
            }
        });

        motos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMotos = new Intent(MainActivity.this, MotocicletasActivity.class);
                startActivity(intentMotos);
            }
        });
    }
}
