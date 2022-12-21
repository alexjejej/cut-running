package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.raywenderlich.android.runtracking.R

enum class ProviderType {
    /** Nos sirve para designar que metodo de autenticacion estamos utilizando
     * De momento tiene le valor BASIC, ya que es el unico tipo de autenticacion que se tiene
     * el cual es una autenticacion basica por email y password **/
    BASIC, GOOGLE;
}

class HomeActivity : AppCompatActivity() {
    lateinit var txtvwEmail: TextView
    lateinit var txtvwPass: TextView
    lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Recuperacion de los paramaetros enviados desde el login
        var bundle = intent.extras
        var email = bundle?.getString("email")
        var provider = bundle?.getString("provider").toString()

        // Setup
        setup( email ?: "", provider ?:"" )

        /** Guardado de los datos del usuario que se ha autenticado a nivel de sesion de la app
         * para que si el usuario ya se encuentra autenticado no nos pida iniciar sesion
         * de nuevo en el login**/
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE) // Se lee el name el file desde el archivo strings.xml
        with( prefs.edit() ) {
            // Es preferible esitar las preferencias dentro de un bloque "with"
            putString("email", email)
            putString("provider", provider)
            apply()
        }
    }

    private fun setup( email: String, provider: String ) {
        txtvwEmail = findViewById(R.id.txtvwEmail)
        txtvwPass = findViewById(R.id.txtvwProvider)
        btnLogout = findViewById(R.id.btnLogout)

        title = "Inicio"

        txtvwEmail.text = email
        txtvwPass.text = provider

        // Boton de logout
        btnLogout.setOnClickListener{
            // Borrado de datos
            val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            with( prefs.edit() ) {
                clear() // Borra todas las preferencias que se encuentran guardadas en el archivo
                apply()
            }

            FirebaseAuth.getInstance().signOut()
            onBackPressed() // Nos devuelve a la pantalla anterior, en este caso el login (AuthActivity)
        }
    }
}