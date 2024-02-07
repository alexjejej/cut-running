package com.cut.android.running.models

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.cut.android.running.R
import com.cut.android.running.provider.resources.StringResourcesProvider

object Session {
    private lateinit var _userName: String
    private lateinit var _userEmail: String
    private lateinit var _userPhoto: Uri
    private lateinit var _prefs: SharedPreferences
    private lateinit var _stringProvider: StringResourcesProvider

    val userName: String        get() = _userName
    val userEmail: String       get() = _userEmail
    val userPhoto: Uri          get() = _userPhoto
    val activeSession: Boolean  get() = _validateSesstion()

    fun setUserName( userName: String )     { _userName = userName }
    fun setUserEmail( userEmail: String )   { _userEmail = userEmail }
    fun setUserPhoto( userPhoto: Uri )      { _userPhoto = userPhoto }

    /** Valida si una sesion se encuentra activa cuando el archivo de preferencias contienen el nombre y el correo **/
    private fun _validateSesstion(): Boolean = !_userName.isNullOrEmpty() && !_userEmail.isNullOrEmpty()

    /** Lectura del archivo sharedpreferences para obtener la informacion del usuario logueado **/
    fun readPrefs( activity: Activity ) {
        _stringProvider = StringResourcesProvider( activity.applicationContext )
        _prefs = activity.getSharedPreferences( _stringProvider.getString(R.string.prefs_file), Context.MODE_PRIVATE)

        setUserName( _prefs.getString( _stringProvider.getString(R.string.prefs_user_name), "" )!! )
        setUserEmail( _prefs.getString( _stringProvider.getString(R.string.prefs_email), "" )!! )
        setUserPhoto( Uri.parse( _prefs.getString( _stringProvider.getString(R.string.prefs_user_photo), "")!! ) )
    }

    /** Elimina el contenido del archivo de preferencias y esblece como vacias las propiedades de la sesion **/
    fun signOut() {
        with(_prefs.edit()) {
            clear()
            commit()
        }
        _userName = ""
        _userEmail = ""
        _userPhoto = Uri.EMPTY
    }
}