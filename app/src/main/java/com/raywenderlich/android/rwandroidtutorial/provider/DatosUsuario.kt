package com.raywenderlich.android.rwandroidtutorial.provider

import android.app.Activity
import android.content.Context
import android.util.Log
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.Session

class DatosUsuario {


    companion object {

        fun getEmail(activity: Activity): String {

            Session.readPrefs(activity)
            val email = Session.userEmail


            return email
        }

        fun getUserName(context: Context): String? {
            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
            return prefs.getString(context.getString(R.string.prefs_user_name), null)
        }
    }


}