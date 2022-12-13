package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.raywenderlich.android.runtracking.R

class AuthActivity : AppCompatActivity() {
    lateinit var txtEmail: EditText
    lateinit var txtPass: EditText
    lateinit var btnLogin: Button
    lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Setup
        setup()
    }

    // TODO: Refactorizar metodo
    private fun setup() {
        txtEmail = this.findViewById(R.id.txtEmail)
        txtPass = this.findViewById(R.id.txtPassword)
        btnLogin = this.findViewById(R.id.btnLogin)
        btnSignUp = this.findViewById(R.id.btnSignUp)

        title = "Authenticcion" // Modificamos el titulo de la pantalla

        // Boton de signup
        btnSignUp.setOnClickListener {
            if ( !(txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty()) ) {
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(txtEmail.text.toString(), txtPass.text.toString()) // Crea un usuario con las credenciales dadas como parametros
                    .addOnCompleteListener {
                        // "addOnCompleteListener" ayuda a verificar si el registro fue exitoso
                        if ( it.isSuccessful ) {
                            // Navegacion a la pantalla Home (HomeActivity)
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }
                        else {
                            // No se completo el registro correctamente
                            showAlert()
                        }
                    }
            }
        }

        // Boton de login
        btnLogin.setOnClickListener{
            if ( txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty() ) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(txtEmail.text.toString(), txtPass.text.toString()) // Crea un usuario con las credenciales dadas como parametros
                    .addOnCompleteListener {
                        // "addOnCompleteListener" ayuda a verificar si el registro fue exitoso
                        if ( it.isSuccessful ) {
                            // Navegacion a la pantalla Home (HomeActivity)
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }
                        else {
                            // No se completo el registro correctamente
                            showAlert()
                        }
                    }
            }
        }
    }

    /** Muestra una laerta de error **/
    private fun showAlert() {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autenticar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /** Muestra la pantalla de Home (HomeActivity) **/
    private fun showHome( email: String, provider: ProviderType ) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply{
            // Paso de parametros a la nueva pantalla que se mostrara
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}