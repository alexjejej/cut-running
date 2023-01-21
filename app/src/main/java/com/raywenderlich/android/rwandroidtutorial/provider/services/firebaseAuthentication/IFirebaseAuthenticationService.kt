package com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication

interface IFirebaseAuthenticationService {

    /** Verificar conexion del usuario **/
    fun curretnUser()

    /** Inicio de sesion **/
    fun signIn(email: String, pass: String)

    /** Creacion de una cuenta **/
    fun signUpWithCreadentials(email: String, pass: String)

    /** Inicio de sesion con Google **/
    fun signIngWithGoogle()
}