package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.PrincipaClasificacion

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

//    private fun loadBlankFragment() {
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            addToBackStack(null)
//            replace(R.id.container_fragment_home, BlankFragment())
//        }
//    }

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