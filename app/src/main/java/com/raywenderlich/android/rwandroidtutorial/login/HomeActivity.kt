package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.raywenderlich.android.runtracking.R

enum class ProviderType {
    /** De momento tiene le valor BASIC, ya que es el unico tipo de autenticacion que se tiene
     * el cual es una autenticacion basica por email y password **/
    BASIC
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
        var pass = bundle?.getString("provider").toString()

        // Setup
        setup( email ?: "", pass ?:"" )
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
            FirebaseAuth.getInstance().signOut()
            onBackPressed() // Nos devuelve a la pantalla anterior, en este caso el login (AuthActivity)
        }
    }
}