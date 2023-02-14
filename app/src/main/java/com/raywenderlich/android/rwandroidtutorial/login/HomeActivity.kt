package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.ListaDatosUsuario
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ClasificacionFragment
import com.raywenderlich.android.rwandroidtutorial.clasificacion.PrincipaClasificacion
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    lateinit var nbBottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        if ( savedInstanceState == null ) { // TODO: Revisar lo que es el savedIntanceState
//            supportFragmentManager.commit {
                /**La transaccion del fragmento solo se crea cuando "savedInstaceState" es null. Esto  para
                 * garantizar que el fragmento se agregue  una sola vez, cuando se crea  la actividad por
                 * primera vez**/
//                setReorderingAllowed(true)
//                addToBackStack(null)
//                add<HomeFragment>(R.id.container_fragment_home) /**Creacion de instancia de  fragmeto**/
//            }
//        }

        this.setup()
        //sincronizar datos:
        sincronizar()
    }

    override fun onBackPressed() {
        Log.d("HomeActivity", "On Back Pressed")
        super.onBackPressed()
    }

    private fun setup() {
        nbBottomNavigationBar = findViewById(R.id.buttonNavbar)

        nbBottomNavigationBar.setOnItemSelectedListener { item -> this.setNavigation(item) }
    }

    private fun setNavigation(item: MenuItem): Boolean {
        when( item.itemId ) {
            R.id.btnHome -> {
                return true
            }
            R.id.btnAddTraining -> {
                this.showMapsActivity()
                return true
            }
            R.id.btnAchievements -> {
                this.showLogrosActivity()
                return true
            }
            R.id.btnClasificacion -> {
                this.showClasificacionActivty()
                return true
            }
            else -> return false
        }
    }

    private fun showClasificacionActivty() {
        this.manageBackStack()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_fragment_home, ClasificacionFragment())
            addToBackStack("ClassificationFragment")
        }
//        var clasificacionIntent = Intent(this, PrincipaClasificacion::class.java)
//        startActivity(clasificacionIntent)
    }

    private fun showLogrosActivity(){
        this.manageBackStack()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_fragment_home, LogrosFragment())
            addToBackStack("AchievementsFragment")
        }
//        var logrosIntent = Intent(this, PrincipalLogros::class.java)
//        startActivity(logrosIntent)
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

    private fun manageBackStack() {
        supportFragmentManager.popBackStack()
        Log.d("BackStack", "${supportFragmentManager.backStackEntryCount}")
    }
}