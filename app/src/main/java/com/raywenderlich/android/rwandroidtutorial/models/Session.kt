package com.raywenderlich.android.rwandroidtutorial.models

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.provider.resources.StringResourcesProvider

object Session {
    private lateinit var _userName: String
    private lateinit var _userEmail: String
    private lateinit var _userPhoto: Uri
    private lateinit var _prefs: SharedPreferences
    private lateinit var _stringProvider: StringResourcesProvider

    val userName: String    get() = _userName
    val userEmail: String   get() = _userEmail
    val userPhoto: Uri      get() = _userPhoto

    fun setUserName( userName: String )     { _userName = userName }
    fun setUserEmail( userEmail: String )   { _userEmail = userEmail }
    fun setUserPhoto( userPhoto: Uri )      { _userPhoto = userPhoto }

    /** Lectura del archivo sharedpreferences para obtener la informacion del usuario logueado **/
    fun readPrefs( activity: Activity ) {
        _stringProvider = StringResourcesProvider( activity.applicationContext )
        _prefs = activity.getSharedPreferences( _stringProvider.getString(R.string.prefs_file), Context.MODE_PRIVATE)

        setUserName( _prefs.getString( _stringProvider.getString(R.string.prefs_user_name), "" )!! )
        setUserEmail( _prefs.getString( _stringProvider.getString(R.string.prefs_email), "" )!! )
        setUserPhoto( Uri.parse( _prefs.getString( _stringProvider.getString(R.string.prefs_user_photo), "")!! ) )
    }
}