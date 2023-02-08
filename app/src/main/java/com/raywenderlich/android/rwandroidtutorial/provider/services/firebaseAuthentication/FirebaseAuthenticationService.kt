package com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.provider.services.resources.StringResourcesProvider
import com.raywenderlich.android.rwandroidtutorial.utils.dialog.Dialog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class FirebaseAuthenticationService @Inject constructor(
    private val _stringResourcesProvider: StringResourcesProvider,
    private val _dialog: Dialog,
) {
    // Creacion de intancia de FirebaseAuth
    private val auth: FirebaseAuth = Firebase.auth
    private val REQUEST_ONE_TAP = 2 // Puede ser cualquier entero unico para el Activity

    /** Verificar conexion del usuario **/
    fun curretnUser(): FirebaseUser? {
        val currentUser = auth.currentUser
        if ( currentUser != null ) {
            return currentUser
        }
        return null
    }

    /** Inicio de sesion **/
    fun signIn(email: String, pass: String): FirebaseUser? {
        var user: FirebaseUser? = null
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    user = auth.currentUser
                    Log.d("FirebaseAuth", "${user?.displayName}")
                    Log.d("FirebaseAuth", "${user?.email}")
                    Log.d("FirebaseAuth", "${user?.photoUrl}")
                    Log.d("FirebaseAuth", "Inicio de sesion exitoso")
                }
                else {
                    _dialog.infoDialog(R.string.info_incorrect_credentials_message, R.string.info_incorrect_credentials_title)
                }
            }
        return if (user != null) user else null
    }

    /** Creacion de una cuenta **/
    fun signUpWithCreadentials(email: String, pass: String): FirebaseUser? {
        var user: FirebaseUser? = null
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    user = auth.currentUser
                    Log.d("FirebaseAuth", "Ususario creado correctamente")
                }
                else {
                    _dialog.warningDialog(R.string.warning_creation_user_message, R.string.warning_creation_user_title)
                    Log.w("FirebaseAuth", "${it.exception.toString()}")
                }
            }
        return if (user != null) user else null
    }

    /** Manejo de la respuesta del activity de seleccion de cuenta de google (oneTapClient) **/
    fun activityResultActions(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        oneTapClient: SignInClient
    ) {
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