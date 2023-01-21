package com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FirebaseAuthenticationService() : IFirebaseAuthenticationService {
    // Creacion de intancia de FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun curretnUser() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if ( currentUser != null ) {
            // TODO: Acciones cuando el usuraio actual no es null
        }
    }

    override fun signIn(email: String, pass: String) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener {
                if ( it.isSuccessful ) {
                    val user = auth.currentUser
                    Log.d("FirebaseAuth", "Inicio de sesion exitoso")
                }
                else {
                    Log.w("FirebaseAuth", "Error al iniciar sescion conlas creadenciales")
                }
            }
    }

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
                }
            }
    }

    override fun signIngWithGoogle() {
        TODO("Not yet implemented")
    }
}