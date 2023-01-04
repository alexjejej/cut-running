package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity

class HomeActivity : AppCompatActivity() {
    lateinit var btnHome:           BottomNavigationItemView
    lateinit var btnAddTraining:    BottomNavigationItemView
    lateinit var btnAchievements:   BottomNavigationItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.setup()
    }

    private fun setup() {
        btnHome = findViewById(R.id.btnHome)
        btnAddTraining = findViewById(R.id.btnAddTraining)
        btnAchievements = findViewById(R.id.btnAchievements)

        btnAddTraining.setOnClickListener { this.showMapsActivity() }
    }

    private fun showMapsActivity() {
        var mapsIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapsIntent)
    }
}