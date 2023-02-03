package com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication

import com.google.firebase.auth.FirebaseUser

interface IFirebaseAuthenticationService {

    /** Verificar conexion del usuario **/
    fun curretnUser(): FirebaseUser?

    /** Inicio de sesion **/
    fun signIn(email: String, pass: String): Boolean

    /** Creacion de una cuenta **/
    fun signUpWithCreadentials(email: String, pass: String): Boolean

    /** Inicio de sesion con Google **/
    fun signIngWithGoogle()
}