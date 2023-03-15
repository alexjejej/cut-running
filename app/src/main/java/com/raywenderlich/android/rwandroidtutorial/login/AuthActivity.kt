package com.raywenderlich.android.rwandroidtutorial.login

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.ActivityAuthBinding
import com.raywenderlich.android.rwandroidtutorial.models.Session
import com.raywenderlich.android.rwandroidtutorial.models.User

class AuthActivity : AppCompatActivity() {
//    @Inject lateinit var _firebaseAuthenticationService: FirebaseAuthenticationService
//    @Inject lateinit var _stringResourcesProvider: StringResourcesProvider
//    @Inject lateinit var _contextProvider: ContextProvider


    // private val authViewModel: AuthViewModel by viewModels()
    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth:  FirebaseAuth
    private lateinit var prefs: SharedPreferences
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private lateinit var authLayout:    LinearLayout

    private val REQUEST_ONE_TAP = 2 // Puede ser cualquier entero unico para el Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        _contextProvider.setContext(this)

        // Setup
        this.setup()
        this.session()
        this.initialiceGoogleutentication()
    }

    override fun onStart() { // Se invoca cada vez que se vuelva a mostrar la pantalla
        super.onStart()
        // Mostramos de nuevo el layout en caso de que hagamos un log out y regresemos a este activity (pantalla)
        this.authLayout.visibility = View.VISIBLE // Hacemos visible el layout
    }

    /** Comprobacion de si existe una sesion activa **/
    private fun session() {
        val userName: String?      = this.prefs.getString(getString(R.string.prefs_user_name), null)
        val email: String?         = this.prefs.getString(getString(R.string.prefs_email), null)

        if ( email != null && userName != null ) {
            this.authLayout.visibility = View.INVISIBLE // Hacemos invisible el layout
            this.showHomeScreen()
        }
    }

    /** Inicializacion de variables y configuraciones iniciales **/
    private fun setup() {
        /** Inicializacion de variables **/
        this.prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        this.auth = Firebase.auth
        this.authLayout  = this.binding.authLayout
        title = "Authenticacion" // Modificamos el titulo de la pantalla

        this.binding.btnGoogleSignIn.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener {result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQUEST_ONE_TAP, null, 0, 0, 0, null
                        )
                    }
                    catch (e: IntentSender.SendIntentException) {
                        Log.e("FirebaseAuth", "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener {
                    Log.d("FirebaseAuth", "${it.localizedMessage}")
                }
        }
    }

    /** Guarda los datos de usuario en el archivo de SharedPreferences **/
    private fun saveSharedPreferences(userName: String, email: String, userPhoto: String) {
        with(this.prefs.edit()) {
            putString(getString(R.string.prefs_user_name), userName)
            putString(getString(R.string.prefs_email), email)
            putString(getString(R.string.prefs_user_photo), userPhoto)
            apply()
        }
        this.showHomeScreen()
    }

    /** Navegamos a la pagina Home de la aplicacion **/
    private fun showHomeScreen() {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    /** Muestra una laerta de error **/
    /*
    private fun showAlert(message: String) {
        var builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage( message )
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
     */

    /** Muestra la pantalla par terminar el registro (RegistrationCompletionActivity) **/
    private fun showRegistrationForm( email: String, provider: ProviderType ) {
        val registrationIntent = Intent(this, RegistrationCompletionActivity::class.java).apply{
            // Paso de parametros a la nueva pantalla que se mostrara
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(registrationIntent)
    }

    /** Este metodo es llamada despues de que se selecciona la cuenta con la que se
     * iniciara sesion con oneTapClient. Es la el metodo donde se valida la respueta
     * del oneTapClient **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // TODO: Consultar documentacion sobre onActivityResult
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with Firebase.
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener {task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        // TODO: Set currente user un firebase aut property
                                        this.saveSharedPreferences(auth.currentUser?.displayName!!, auth.currentUser?.email!!, auth.currentUser?.photoUrl.toString())
                                        Session.setUserName( auth.currentUser?.displayName!! )
                                        Session.setUserEmail( auth.currentUser?.email!! )
                                        Session.setUserPhoto( auth.currentUser?.photoUrl!! )
                                        Log.d("FirebaseAuth", "Got ID token.")
                                        Log.d("FirebaseAuth", "${auth.currentUser?.photoUrl}")
                                    }
                                    else {
                                        Log.w("FirebaseAuth", "${task.exception.toString()}")
                                    }
                                }
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d("FirebaseAuth", "No ID token!")
                        }
                    }
                }
                catch (e: ApiException) {
                    Log.d("FirebaseAuth", "${e.message.toString()}")
                }
            }
        }
    }

    /** Crea el registro correspondiente en la colecion "users" de Firestore **/
    private fun createUser( email: String, provider: ProviderType ) {
        val db = Firebase.firestore // Referencia a la DB Cloud Firestore definida en Firebase
        db.collection("users").document(email)
            .set(
                User(
                    cu = "",
                    career = "",
                    completeInformation = false,
                    provider = provider.name,
                    enable = true,
                    semester = 1
                )
            )
            .addOnSuccessListener {
                Log.d("Registro exitoso", "Datos del usuario agregados correctamente")
            }
            .addOnFailureListener {
                Log.w("Registro fallido", "No se ha logrado realizar el registro de los datos")
            }
    }

    /** Inicializacion de propiedades para usar el oneTapClient de inicio de sesion
     * com Google **/
    private fun initialiceGoogleutentication() {
        oneTapClient = Identity.getSignInClient(this)

        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(
                        getString(R.string.default_web_client_id)
//                        _stringResourcesProvider.getString() TODO: Delete
                    )
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }
}