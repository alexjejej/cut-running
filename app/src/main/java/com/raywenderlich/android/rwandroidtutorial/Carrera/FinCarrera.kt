package com.raywenderlich.android.rwandroidtutorial.Carrera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.text.SimpleDateFormat
import java.util.*


class FinCarrera : AppCompatActivity() {
    val chanelID = "logros"
    val chanelName = "logros"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_carrera)
        ProcesarDatos()

    }

    private fun ProcesarDatos(){
        var nuevospasos: Int
        var nuevadistancia: Float
        var DatosUsuario: ListaDatosUsuario
        val txtPasos = findViewById<TextView>(R.id.txtPasos)
        val txtDistancia = findViewById<TextView>(R.id.txtDistancia)

        //fecha hoy
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        //variables locales
        val sharedPreference =  getSharedPreferences("Datos",Context.MODE_PRIVATE)
        var pasos = sharedPreference.getInt("pasos",0)
        var distancia = sharedPreference.getFloat("distancia",0F)
        var pasosT = sharedPreference.getInt("PasosTotales",0)

        //obtener usuario
        val usuario = "alex"

        //Obtener datoa del usuario y sumarlos con los nuevos
        val database = Firebase.database
        database.getReference("users").child(usuario).child("datos").
                get().addOnSuccessListener {

                    if (it.exists()){
                        val bdPasosT = it.child("pasosT").getValue(Int::class.java)
                        val bdDistancia = it.child("distanciaT").getValue(Float::class.java)
                        nuevospasos = bdPasosT!!+pasos
                        nuevadistancia = distancia+bdDistancia!!
                        DatosUsuario = ListaDatosUsuario((nuevospasos),(nuevadistancia))
                        val myRef = database.getReference("users").child(usuario).child("datos")
                        myRef.setValue(DatosUsuario)
                    }else{
                        Log.d("Datos no encontrados: ","No existe")
                        CrearDatos()
                    }


                }.addOnFailureListener{
                    Log.e("firebase", "Error getting data", it)
                }

        txtPasos.text = (pasos.toString()+" pasos")
        txtDistancia.text = (distancia.toString()+ " metros")
        consultarlogro(pasosT)
        clasificacion(pasosT,usuario)


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



}