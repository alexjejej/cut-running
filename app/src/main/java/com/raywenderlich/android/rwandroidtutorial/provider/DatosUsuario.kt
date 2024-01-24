package com.raywenderlich.android.rwandroidtutorial.provider

import android.app.Activity
import android.content.Context
import android.util.Log
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.Session

class DatosUsuario {

    fun saveUserName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(context.getString(R.string.prefs_user_name), name)
            apply()
        }
    }

    fun getUserName(context: Context): String? {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        return prefs.getString(context.getString(R.string.prefs_user_name), null)
    }

    companion object {
        fun getUserName(context: Context): String? {
            val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
            return prefs.getString(context.getString(R.string.prefs_user_name), null)
        }
        fun getPasosTotales(context: Context): Int {
            // Obtener nombre de usuario
            val userName = getUserName(context) ?: "NombrePorDefecto"

            //Obtener datos de sqlite
            val db = BDsqlite(context)
            // Obtener datos para el usuario
            val cursorPasosTotales = db.getData(BDsqlite.getColumnPasosTotales(), userName)
            cursorPasosTotales.moveToFirst()
            val pasosBD = cursorPasosTotales.getInt(0)


            return pasosBD
        }
        fun getEmail(activity: Activity): String {

            Session.readPrefs(activity)
            val email = Session.userEmail


            return email
        }
    }


}