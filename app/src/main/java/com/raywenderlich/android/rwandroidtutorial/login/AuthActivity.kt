package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.raywenderlich.android.runtracking.R

class AuthActivity : AppCompatActivity() {
    lateinit var txtEmail:      EditText
    lateinit var txtPass:       EditText
    lateinit var btnLogin:      Button
    lateinit var btnSignUp:     Button
    lateinit var btnGoogleSignin: Button
    lateinit var authLayout:    LinearLayout

    private val GOOGLE_SIGN_IN = 100 // Identificador que se envia a "startActivityForResult" para asi recoger la respuesta de la autenticacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Setup
        setup()
        session()
    }

    override fun onStart() { // Se invoca cada vez que se vuelva a mostrar la pantalla
        super.onStart()
        // Mostramos de nuevo el layout en caso de que hagamos un log out y regresemos a este activity (pantalla)
        authLayout.visibility = View.VISIBLE // Hacemos visible el layout
    }

    /** Comprobacion de si existe una sesion activa **/
    private fun session() {
        val prefs: SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String?      = prefs.getString("email", null)
        val provider: String?   = prefs.getString("provider", null)

        if ( email != null && provider != null ) {
            authLayout.visibility = View.INVISIBLE // Hacemos invisible el layout
            showHome(email ?: "", ProviderType.valueOf(provider ?: ""))
        }
    }

    // TODO: Refactorizar metodo
    private fun setup() {
        txtEmail    = this.findViewById(R.id.txtEmail)
        txtPass     = this.findViewById(R.id.txtPassword)
        btnLogin    = this.findViewById(R.id.btnLogin)
        btnSignUp   = this.findViewById(R.id.btnSignUp)
        btnGoogleSignin = this.findViewById(R.id.btnGoogleSignIn)
        authLayout  = this.findViewById(R.id.authLayout)

        title = "Authenticcion" // Modificamos el titulo de la pantalla

        // Boton de signup
        btnSignUp.setOnClickListener {
            if ( txtEmail.text.isNotEmpty() && txtPass.text.isNotEmpty() ) {
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

        // Boton de Google sign in
        btnGoogleSignin.setOnClickListener {
            // Configuracion de login con Google
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // Indica que se usara el login por defecto de la cuenta de google
                .requestIdToken(getString(R.string.default_web_client_id)) // Pasamos el token Id asociado a nuestra app
                .requestEmail() // Solicitamos el email de la persona que se esta logueando
                .build()

            // Cliente de autenticacion de google
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut() // Para que cada vez que se inicie sesion con google se realice el log out de la cuenta que pueda estar autenticada. Esto
            // es util para el caso de que se tenga mas de una cuenta de google asociada al dispositivo

            // Mostrar la pantalla de autenticacion de Google
            // val getContent = registerForActivityResult(googleClient.signInIntent)
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN) // TODO: Buscar implementacion reciente
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

    /** Deprecated **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // TODO: Consultar documentacion sober onActivityResult
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == GOOGLE_SIGN_IN ) {
            // Si el request code es igual al reques code definido en GOOGLE_SIGN_IN querra decir
            // que la respuesta de este activity se corresponde con el de la autenticacion con Google
            if ( requestCode == GOOGLE_SIGN_IN ) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    // Esta linea puede producir un error en caso de que no sea capaz de recuperar una cuenta
                    val account = task.getResult(ApiException::class.java)

                    if ( account != null ) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        FirebaseAuth.getInstance()
                            .signInWithCredential(credential) // Pasamos la autenticacion a Firebase para crear un registro
                            .addOnCompleteListener{
                                if ( it.isSuccessful ) {
                                    showHome(account.email ?: "", ProviderType.GOOGLE)
                                }
                                else {
                                    showAlert()
                                }
                            }
                    }
                }
                catch ( ex: ApiException ) {
                    showAlert()
                }
            }
        }
    }
}