package com.raywenderlich.android.rwandroidtutorial.login.viewmodel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.SignInClient
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication.FirebaseAuthenticationService
import com.raywenderlich.android.rwandroidtutorial.provider.services.resources.StringResourcesProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val _firebaseAuthenticationService: FirebaseAuthenticationService,
    private val _stringResourcesProvider: StringResourcesProvider
) : ViewModel() {

    /** Metodo de inicio de sesion con credenciales **/
    fun signIn(email: String, pass: String) {
        _firebaseAuthenticationService.signIn( email.buildCompleteEmail(), pass )
    }

    /** Metodo de creacion de usuario con las credenciales ingresadas **/
    fun signUp(email: String, pass: String) =
        _firebaseAuthenticationService
            .signUpWithCreadentials(email.buildCompleteEmail(), pass)

    fun signIngWithGoogle(applicationContext: AppCompatActivity){}

    /** Llama al metodo que maneja la respuesta del activity de seleccion de cuenta de google (oneTapClient) **/
    fun onActivityResultActions(requestCode: Int, resultCode: Int, data: Intent?, oneTapClient: SignInClient) =
        _firebaseAuthenticationService.onActivityResultActions(requestCode, resultCode, data, oneTapClient)

    /** Metodo de extencion que construlle el correo completo
     * tomando en cuenta el suffix establecido en el EditText **/
    fun String.buildCompleteEmail(): String =
        this + _stringResourcesProvider.getString(R.string.txt_udg_suffix)
}