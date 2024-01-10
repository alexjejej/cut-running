package com.raywenderlich.android.rwandroidtutorial.Carrera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.ListaNotificacion
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ListaClasificacion
import com.raywenderlich.android.rwandroidtutorial.login.HomeActivity
import com.raywenderlich.android.rwandroidtutorial.provider.BDsqlite
import java.text.SimpleDateFormat
import java.util.*


class FinCarrera : AppCompatActivity() {
    val chanelID = "logros"
    val chanelName = "logros"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_carrera)
        animateViews()
        ProcesarDatos()

    }

    private fun ProcesarDatos(){

        val txtPasos = findViewById<TextView>(R.id.txtPasos)
        val txtDistancia = findViewById<TextView>(R.id.txtDistancia)
        val txtPasosT = findViewById<TextView>(R.id.txtpasosT)

        //Obtener datos de sqlite
        val db = BDsqlite(this)
        // Obtener datos para el usuario "Alex"
        val cursorPasosHoy = db.getData(BDsqlite.getColumnPasosHoy(), "Alex")
        val cursorPasosTotales = db.getData(BDsqlite.getColumnPasosTotales(), "Alex")
        val cursorDistancia = db.getData(BDsqlite.getColumnDistancia(), "Alex")

        // Leer y mostrar los datos de cada consulta
        if (cursorPasosHoy.moveToFirst()) {
            val pasosHoy =
                cursorPasosHoy.getInt(0) // El índice 0 representa la primera columna del resultado
            Log.d("DBData", "Pasos Hoy para Alex: $pasosHoy")
            txtPasos.text = (pasosHoy.toString()+" pasos")
        }

        if (cursorPasosTotales.moveToFirst()) {
            val pasosTotales = cursorPasosTotales.getInt(0)
            Log.d("DBData", "Pasos Totales para Alex: $pasosTotales")
            consultarlogro(pasosTotales)
            txtPasosT.text = (pasosTotales.toString()+" pasos")
        }

        if (cursorDistancia.moveToFirst()) {
            val distancia = cursorDistancia.getFloat(0)
            Log.d("DBData", "Distancia para Alex: $distancia")
            txtDistancia.text = (distancia.toString()+ " metros")
        }



        cursorPasosHoy.close()
        cursorPasosTotales.close()
        cursorDistancia.close()

        //clasificacion(pasosT,usuario)


    }

    private fun clasificacion(pasosT: Int, usuario: String) {
        //Creador de logros aleatorios
        val database = Firebase.database
        val myRef = database.getReference("clasificacion").child("historica").child(""+usuario)
        val lista = ListaClasificacion("0",""+usuario,pasosT)
        myRef.setValue(lista)

    }

    private fun CrearDatos() {

        //fecha hoy
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        //variables locales
        val sharedPreference =  getSharedPreferences("Datos",Context.MODE_PRIVATE)
        var pasos = sharedPreference.getInt("pasos",0)
        var distancia = sharedPreference.getFloat("distancia",0F)
        //obtener usuario
        val usuario = "alex"
        val database = Firebase.database
        val myRef = database.getReference("users").child(usuario).child("datos")
        val DatosUsuario = ListaDatosUsuario(pasos,distancia)
        myRef.setValue(DatosUsuario)



    }

    private fun consultarlogro(pasosT: Int) {
        //obtener usuario
        val usuario = "alex"

        val database = Firebase.database
        database.getReference("logros").
        get().addOnSuccessListener {

            for (snapshot in it.children) {
                var tituloL = snapshot.child("titulo").getValue(String::class.java)
                var pasosL = snapshot.child("pasos").getValue(Int::class.java)
                if (pasosL!!<=pasosT){
                    Log.d("Estos son los logros obtenidos: ",""+snapshot.child("titulo").getValue(String::class.java))
                    Consultarnotificacion(pasosL,tituloL!!)
                }else{
                    Log.d("LOG No se obtuvieron logros ","Error"+pasosT)
                }
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun Consultarnotificacion(pasosL: Int, tituloL: String) {
        //consultar si ya se mostró la notificación
        //obtener usuario
        val usuario = "alex"
        var contador = 0
        val database = Firebase.database
        database.getReference("users").child(usuario).child("notificaciones").
        get().addOnSuccessListener {

            for (snapshot in it.children) {
                var tituloNoti = snapshot.child("titulo").getValue(String::class.java)
                if (tituloNoti==tituloL){
                    Log.d("Estos son los logros obtenidos: ","Ya se obtuvo este logro")
                    contador = 1
                }
            }
            if (contador==0){
                notificacion(pasosL, tituloL)
            }

        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    private fun notificacion(pasosLogro: Int, tituloLogro: String){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // construir canal
            val importance = NotificationManager.IMPORTANCE_DEFAULT // (5)
            val channel = NotificationChannel(chanelID, chanelName, importance)

            //manager de notificaciones
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            //configurando notificacion

            val notificacion = NotificationCompat.Builder(this, chanelID).also { noti ->
                noti.setSmallIcon(R.drawable.logro)
                noti.setContentTitle(""+tituloLogro)
                noti.setContentText("!Felicidades¡ obtuviste un logro por haber realizado un total" +
                        " de "+pasosLogro+" pasos")
                noti.setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }.build()

            val notificationManageer = NotificationManagerCompat.from(applicationContext)
            notificationManageer.notify(1,notificacion);
        }
        RegistrarNotificacion(pasosLogro, tituloLogro)
    }

    private fun RegistrarNotificacion(pasosLogro: Int, tituloLogro: String) {
        //obtener usuario
        var usuario = "alex"
        //Creador de logros aleatorios
        val database = Firebase.database
        //Creador de logros
        val id = database.getReference("users").child(usuario).child("notificaciones")
            .child(""+tituloLogro)
        val noti = ListaNotificacion(""+tituloLogro,"Corre por "+pasosLogro+" pasos",
            "Finalizado",pasosLogro)
        id.setValue(noti)
    }

    fun ReturnHome(view: View) {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun animateViews() {
        val viewsToAnimate = listOf(
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.txtPasos),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.txtDistancia),
            findViewById<TextView>(R.id.textview10),
            findViewById<TextView>(R.id.txtpasosT),
            findViewById<Button>(R.id.btnMenu)
        )

        for ((index, view) in viewsToAnimate.withIndex()) {
            // Desvanecimiento
            view.animate().alpha(1.0f).setDuration(600)
                .setStartDelay((index + 1) * 200L)
                .start()

            // Deslizamiento desde abajo
            view.translationY = 200f
            view.animate().translationY(0f).setDuration(600)
                .setStartDelay((index + 1) * 200L)
                .start()
        }
    }




}