package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.PrincipaClasificacion

class HomeActivity : AppCompatActivity() {
    lateinit var btnHome:           BottomNavigationItemView
    lateinit var btnAddTraining:    BottomNavigationItemView
    lateinit var btnAchievements:   BottomNavigationItemView
    lateinit var btnClasificacion:   BottomNavigationItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.setup()
    }

    private fun setup() {
        btnHome = findViewById(R.id.btnHome)
        btnAddTraining = findViewById(R.id.btnAddTraining)
        btnAchievements = findViewById(R.id.btnAchievements)
        btnClasificacion = findViewById(R.id.btnClasificacion)

        btnAddTraining.setOnClickListener { this.showMapsActivity() }
        btnAchievements.setOnClickListener{this.showLogrosActivity()}
        btnClasificacion.setOnClickListener{this.showClasificacionActivty()}
    }

    private fun showClasificacionActivty() {
        var clasificacionIntent = Intent(this, PrincipaClasificacion::class.java)
        startActivity(clasificacionIntent)
    }

    private fun showLogrosActivity(){
        var logrosIntent = Intent(this, PrincipalLogros::class.java)
        startActivity(logrosIntent)
    }

    private fun showMapsActivity() {
        var mapsIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapsIntent)
    }
}