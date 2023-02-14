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
//import com.raywenderlich.android.rwandroidtutorial.provider.services.resources.StringResourcesProvider
//import com.raywenderlich.android.rwandroidtutorial.utils.dialog.Dialog
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject

//@HiltViewModel
class AuthViewModel (
//    private val _firebaseAuthenticationService: FirebaseAuthenticationService,
//    private val _stringResourcesProvider: StringResourcesProvider,
//    private val _dialog: Dialog
) : ViewModel() {

    val authenticationResult = MutableLiveData<FirebaseUser>()

    // TODO: Eliminar o definir un comportamiento
    fun signIngWithGoogle(applicationContext: AppCompatActivity){}

    /** Llama al metodo que maneja la respuesta del activity de seleccion de cuenta de google (oneTapClient) **/
    fun activityResultActions(requestCode: Int, resultCode: Int, data: Intent?, oneTapClient: SignInClient) {
//        _firebaseAuthenticationService.activityResultActions(requestCode, resultCode, data, oneTapClient)
    }

    /** Metodo de extencion que construlle el correo completo
     * tomando en cuenta el suffix establecido en el EditText **/
//    fun String.buildCompleteEmail(): String =
//        this + _stringResourcesProvider.getString(R.string.txt_udg_suffix)
}