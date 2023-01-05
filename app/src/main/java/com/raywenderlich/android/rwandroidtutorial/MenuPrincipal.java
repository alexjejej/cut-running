package com.raywenderlich.android.rwandroidtutorial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity;
import com.raywenderlich.android.runtracking.R;
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros;
import com.raywenderlich.android.rwandroidtutorial.clasificacion.PrincipaClasificacion;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
    }


    public void btnCarrera(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void btnLogros(View view){
        Intent intent = new Intent(this, PrincipalLogros.class);
        startActivity(intent);
    }
    public void btnClasificacion (View view){
        Intent intent = new Intent(this, PrincipaClasificacion.class);
        startActivity(intent);
    }

}