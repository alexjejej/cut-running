package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.commit
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.ActivityHomeBinding
import com.raywenderlich.android.rwandroidtutorial.Carrera.ListaDatosUsuario
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ClasificacionFragment
import com.raywenderlich.android.rwandroidtutorial.models.Session
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Configuracion de Nav Bar
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

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

        Session.readPrefs( this )
        this.setup()
        this.sincronizar()
    }

    /** Es el evento que se dispara cuadno retrocedemos en la aplicacion (dar clic en el boton atras) **/
    override fun onBackPressed() {
        Log.d("HomeActivity", "On Back Pressed")
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    /** Inicializa la configuracion de los componentes **/
    private fun setup() {
        // Obtencion del nombre de ususario de shared preferences y formateo para obtener los dos primeros valores
        val userName: List<String>? = Session.userName.split(" ")

        // Accedemos a modificar el header del Nav Bar
        var header: View = binding.navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.txtUserName).text = userName?.get(0) + " " + userName?.get(1)
        Picasso.get().load(Session.userPhoto).into(header.findViewById<ImageView>(R.id.profilePhoto))
        binding.navigationView.setNavigationItemSelectedListener { item -> this.setNavigation(item) }
    }

    /** Controla la navegacion del menu **/
    private fun setNavigation(item: MenuItem): Boolean {
        when( item.itemId ) {
            R.id.btnHome -> {
                return true
            }
            R.id.btnAddTraining -> {
                this.showMapsActivity()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.btnAchievements -> {
                this.showLogrosFragment()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.btnClasificacion -> {
                this.showClasificacionFragment()
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            else -> return false
        }
    }

    /** Muestra fragment de mapa y step counter **/
    private fun showMapsActivity() {
        var mapsIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapsIntent)
    }

    /** Muestra fragment de logros **/
    private fun showLogrosFragment(){
        this.manageBackStack()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_fragment_home, LogrosFragment())
            addToBackStack("AchievementsFragment")
        }
//        var logrosIntent = Intent(this, PrincipalLogros::class.java)
//        startActivity(logrosIntent)
    }

    /** Muestra fragment de clasificacion **/
    private fun showClasificacionFragment() {
        this.manageBackStack()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.container_fragment_home, ClasificacionFragment())
            addToBackStack("ClassificationFragment")
        }
//        var clasificacionIntent = Intent(this, PrincipaClasificacion::class.java)
//        startActivity(clasificacionIntent)
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

    /** Maneja el BackStack de fragments para que no se sobrepongan los fragments **/
    private fun manageBackStack() {
        supportFragmentManager.popBackStack()
        Log.d("BackStack", "${supportFragmentManager.backStackEntryCount}")
    }
}