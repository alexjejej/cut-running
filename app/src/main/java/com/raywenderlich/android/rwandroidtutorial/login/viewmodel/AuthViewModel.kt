package com.raywenderlich.android.rwandroidtutorial.login.viewmodel

import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseUser
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.User
import com.raywenderlich.android.rwandroidtutorial.provider.services.firebaseAuthentication.FirebaseAuthenticationService
import com.raywenderlich.android.rwandroidtutorial.provider.services.resources.StringResourcesProvider
import com.raywenderlich.android.rwandroidtutorial.utils.dialog.Dialog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val _firebaseAuthenticationService: FirebaseAuthenticationService,
    private val _stringResourcesProvider: StringResourcesProvider,
    private val _dialog: Dialog
) : ViewModel() {

    val authenticationResult = MutableLiveData<FirebaseUser>()

    /** Metodo de inicio de sesion con credenciales **/
    fun signIn(email: String, pass: String) {
        val currentUser = _firebaseAuthenticationService
            .signIn( email.buildCompleteEmail(), pass )
            // TODO: Enviar valor al live data
    }

    /** Metodo de creacion de usuario con las credenciales ingresadas **/
    fun signUp(email: String, pass: String) {
        val currentUser =_firebaseAuthenticationService
            .signUpWithCreadentials(email.buildCompleteEmail(), pass)
        if (currentUser != null) {
            // TODO: Guardar email en el SharedPreferences
            // TODO: Mandar boolean al LiveData
        }
    }

    // TODO: Eliminar o definir un comportamiento
    fun signIngWithGoogle(applicationContext: AppCompatActivity){}

    /** Llama al metodo que maneja la respuesta del activity de seleccion de cuenta de google (oneTapClient) **/
    fun activityResultActions(requestCode: Int, resultCode: Int, data: Intent?, oneTapClient: SignInClient) {
        val currentUser = _firebaseAuthenticationService
            .activityResultActions(requestCode, resultCode, data, oneTapClient)
        if (currentUser != null) {
            Log.d("AuthActivity", "${currentUser.displayName}")
            authenticationResult.postValue(currentUser!!)
        }
    }

    /** Metodo de extencion que construlle el correo completo
     * tomando en cuenta el suffix establecido en el EditText **/
    fun String.buildCompleteEmail(): String =
        this + _stringResourcesProvider.getString(R.string.txt_udg_suffix)
}