package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.ListaDatosUsuario
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.PrincipaClasificacion
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var btnHome:           BottomNavigationItemView
    lateinit var btnAddTraining:    BottomNavigationItemView
    lateinit var btnAchievements:   BottomNavigationItemView
    lateinit var btnClasificacion:   BottomNavigationItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        this.setup()
        //sincronizar datos:
        sincronizar()
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
    private fun sincronizar() {
        //variables locales
        val sharedPreference =  getSharedPreferences("Datos", Context.MODE_PRIVATE)
        var condicion = sharedPreference.getString("sincronizado","no")
        var editor = sharedPreference.edit()

        //obtener usuario
        val usuario = "alex"

        //Condición de si es la primera vez que se inicia en la app
        if (condicion=="no"){
            //Obtener datoa del usuario y sumarlos con los nuevos
            val database = Firebase.database
            database.getReference("users").child(usuario).child("datos").
            get().addOnSuccessListener {

                if (it.exists()){

                    val bdPasosT = it.child("pasosT").getValue(Int::class.java)
                    val bdDistancia = it.child("distanciaT").getValue(Float::class.java)

                    editor.putInt("PasosTotales",bdPasosT!!)
                    editor.putFloat("distancia",bdDistancia!!)
                    Log.d("Datos encontrados: ","pasos T "+bdPasosT+" Distancia: "+bdDistancia)
                    editor.putString("condicion","si")
                    editor.commit()

                }else{
                    Log.d("Datos no encontrados: ","No existe")
                    CrearDatos()
                }


            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }

    }
    private fun CrearDatos() {
        //fecha hoy
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        //variables locales
        val sharedPreference =  getSharedPreferences("Datos", Context.MODE_PRIVATE)
        var pasos = sharedPreference.getInt("pasos",0)
        var distancia = sharedPreference.getFloat("distancia",0F)
        //obtener usuario
        val usuario = "alex"
        val database = Firebase.database
        val myRef = database.getReference("users").child(usuario).child("datos")
        val DatosUsuario = ListaDatosUsuario(pasos,distancia)
        myRef.setValue(DatosUsuario)
    }
}