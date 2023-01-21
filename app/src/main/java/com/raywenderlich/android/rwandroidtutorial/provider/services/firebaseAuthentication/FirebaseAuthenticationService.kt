package com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.rwandroidtutorial.provider.services.resources.StringResourcesProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class FirebaseAuthenticationService @Inject constructor(
    private val _stringResourcesProvider: StringResourcesProvider,
    @ApplicationContext private val context: Context
) : IFirebaseAuthenticationService {
    // Creacion de intancia de FirebaseAuth
    private lateinit var auth: FirebaseAuth
    private val REQUEST_ONE_TAP = 2 // Puede ser cualquier entero unico para el Activity

    /** Verificar conexion del usuario **/
    override fun curretnUser() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if ( currentUser != null ) {
            // TODO: Acciones cuando el usuraio actual no es null
        }
    }

    /** Inicio de sesion **/
    override fun signIn(email: String, pass: String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    val user = auth.currentUser
                    Log.d("FirebaseAuth", "Inicio de sesion exitoso")
                }
                else {
                    Log.w("FirebaseAuth", "Error al iniciar sesion con las creadenciales")
                    Log.w("FirebaseAuth", "${it.exception.toString()}")
                }
            }
    }

    /** Creacion de una cuenta **/
    override fun signUpWithCreadentials(email: String, pass: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    val user = auth.currentUser
                    Log.d("FirebaseAuth", "Ususario creado correctamente")
                }
                else {
                    Log.w("FirebaseAuth", "Error en la creacion del usuario")
                    Log.w("FirebaseAuth", "${it.exception.toString()}")
                }
            }
    }

    /** Inicio de sesion con Google **/
    override fun signIngWithGoogle() {}

    /** Manejo de la respuesta del activity de seleccion de cuenta de google (oneTapClient) **/
    fun onActivityResultActions(requestCode: Int, resultCode: Int, data: Intent?, oneTapClient: SignInClient) {
        auth = Firebase.auth
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
                                        Log.d("FirebaseAuth", "Got ID token.")
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
}