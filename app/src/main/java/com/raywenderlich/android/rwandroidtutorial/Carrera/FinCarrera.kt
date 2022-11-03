package com.raywenderlich.android.rwandroidtutorial.Carrera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.MenuPrincipal
import java.text.SimpleDateFormat
import java.util.*


class FinCarrera : AppCompatActivity() {
    val chanelID = "logros"
    val chanelName = "logros"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_carrera)
        valoresIniciales()

    }

    private fun valoresIniciales(){
        //fecha hoy
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        //variables locales
        val sharedPreference =  getSharedPreferences("Datos",Context.MODE_PRIVATE)

        var pasos = sharedPreference.getInt("Pasos "+currentDate,0)
        var distancia = sharedPreference.getFloat("Distancia "+currentDate,0F)
        var pasosT = sharedPreference.getInt("PasosTotales",0)


        val txtPasos = findViewById<TextView>(R.id.txtPasos)
        val txtDistancia = findViewById<TextView>(R.id.txtDistancia)


        txtPasos.text = (pasos.toString()+" pasos")
        txtDistancia.text = (distancia.toString()+ " metros")

        notificacion(pasosT)

    }

    private fun notificacion(pasosTotales: Int){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // construir canal
            val importance = NotificationManager.IMPORTANCE_DEFAULT // (5)
            val channel = NotificationChannel(chanelID, chanelName, importance)

            //manager de notificaciones
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            //configurando notificacion

            val notificacion = NotificationCompat.Builder(this, chanelID).also { noti ->
                noti.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                noti.setContentTitle("Logro")
                noti.setContentText("Has obtenido la medalla de: "+pasosTotales+" pasos")
                noti.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }.build()

            val notificationManageer = NotificationManagerCompat.from(applicationContext)
            notificationManageer.notify(1,notificacion);
        }
    }

    private fun logros(pasostotales: Int) {
        /*if (pasostotales >= 105) {
          Log.d("logro: ", "Felicidades, obtuviste la medalla de 100 pasos");

          notificacion (105)
        }
        if (pasostotales >= 120) {
          Log.d("logro: ", "Felicidades, obtuviste la medalla de 100 pasos");

          notificacion (120)
        }
        if (pasostotales >= 130) {
          Log.d("logro: ", "Felicidades, obtuviste la medalla de 100 pasos");

          notificacion (130)
        }*/// notificacion(pasostotales)
    }

    fun btnMenu(view: View?) {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}