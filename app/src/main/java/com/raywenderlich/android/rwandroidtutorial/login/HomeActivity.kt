package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.Logros.PrincipalLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ClasificacionFragment
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

    private fun manageBackStack() {
        supportFragmentManager.popBackStack()
        Log.d("BackStack", "${supportFragmentManager.backStackEntryCount}")
    }
}