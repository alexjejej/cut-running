package com.cut.android.running.usecases.home

import LogroConseguidoFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cut.android.running.usecases.login.LoginFragment
import com.cut.android.running.R
import com.cut.android.running.databinding.ActivityHomeBinding
import com.cut.android.running.models.Session

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent(intent) // Añade esta llamada aquí
        Session.readPrefs(this)
        this.setup()
        // this.sincronizar()
    }

    private fun handleIntent(intent: Intent) {
        val fragmentoDestino = intent.getStringExtra("fragmentoDestino")
        val nombreLogro = intent.getStringExtra("nombreLogro")
        val pasosLogro = intent.getIntExtra("pasosLogro", 0) // Usar un valor predeterminado
        val lanzadoDesdeNotificacion = intent.getBooleanExtra("lanzadoDesdeNotificacion", false)

        val fragment = when (fragmentoDestino) {
            "LogroConseguidoFragment" -> LogroConseguidoFragment.newInstance(nombreLogro!!, pasosLogro)
            "MapsFragment" -> MapsFragment.newInstance().apply {
                arguments = Bundle().apply {
                    putBoolean("lanzadoDesdeNotificacion", lanzadoDesdeNotificacion)
                }
            }
            else -> LoginFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container_fragment, fragment)
            .commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Asegúrate de que la actividad utilice el nuevo intent
        handleIntent(intent) // Reutiliza el método para manejar el intent
    }





    /** Es el evento que se dispara cuadno retrocedemos en la aplicacion (dar clic en el boton atras) **/
    override fun onBackPressed() {

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }


    /** Maneja el BackStack de fragments para que no se sobrepongan los fragments **/
    private fun manageBackStack() {
        supportFragmentManager.popBackStack()
        Log.d("BackStack", "${supportFragmentManager.backStackEntryCount}")
    }
    fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_container_fragment, fragment)
            addToBackStack(null) // Opcional, si quieres agregar la transacción al back stack
            commit()
        }
    }

    /** Inicializa la configuracion de los componentes **/
    private fun setup() {}

//    private fun sincronizar() {
//        //variables locales
//        val sharedPreference =  getSharedPreferences("Datos", Context.MODE_PRIVATE)
//        var condicion = sharedPreference.getString("sincronizado","no")
//        var editor = sharedPreference.edit()
//
//        //obtener usuario
//        val usuario = "alex"
//
//        //Condición de si es la primera vez que se inicia en la app
//        if (condicion=="no"){
//            //Obtener datoa del usuario y sumarlos con los nuevos
//            val database = Firebase.database
//            database.getReference("users").child(usuario).child("datos").
//            get().addOnSuccessListener {
//
//                if (it.exists()){
//
//                    val bdPasosT = it.child("pasosT").getValue(Int::class.java)
//                    val bdDistancia = it.child("distanciaT").getValue(Float::class.java)
//
//                    editor.putInt("PasosTotales",bdPasosT!!)
//                    editor.putFloat("distancia",bdDistancia!!)
//                    Log.d("Datos encontrados: ","pasos T "+bdPasosT+" Distancia: "+bdDistancia)
//                    editor.putString("condicion","si")
//                    editor.commit()
//
//                }else{
//                    Log.d("Datos no encontrados: ","No existe")
//                    CrearDatos()
//                }
//
//
//            }.addOnFailureListener{
//                Log.e("firebase", "Error getting data", it)
//            }
//        }
//
//    }
//    private fun CrearDatos() {
//        //fecha hoy
//        val sdf = SimpleDateFormat("dd/M/yyyy")
//        val currentDate = sdf.format(Date())
//        //variables locales
//        val sharedPreference =  getSharedPreferences("Datos", Context.MODE_PRIVATE)
//        var pasos = sharedPreference.getInt("pasos",0)
//        var distancia = sharedPreference.getFloat("distancia",0F)
//        //obtener usuario
//        val usuario = "alex"
//        val database = Firebase.database
//        val myRef = database.getReference("users").child(usuario).child("datos")
//        val DatosUsuario = ListaDatosUsuario(pasos,distancia)
//        myRef.setValue(DatosUsuario)
//    }
}