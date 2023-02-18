package com.raywenderlich.android.rwandroidtutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity;
import com.raywenderlich.android.runtracking.R;

public class MenuPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

    }


//    public void btnCarrera(View view) {
//        Intent intent = new Intent(this, MapsActivity.class);
//        startActivity(intent);
//    }
//    public void btnLogros(View view){
//        Intent intent = new Intent(this, PrincipalLogros.class);
//        startActivity(intent);
//    }
//    public void btnClasificacion (View view){
//        Intent intent = new Intent(this, PrincipaClasificacion.class);
//        startActivity(intent);
//    }

}