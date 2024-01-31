package com.raywenderlich.android.rwandroidtutorial.Carrera

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.usecases.HomeActivity
import com.raywenderlich.android.rwandroidtutorial.provider.BDsqlite
import com.raywenderlich.android.rwandroidtutorial.provider.DatosUsuario
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.AchievementService
import com.raywenderlich.android.rwandroidtutorial.provider.services.ClassificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class FinCarrera : AppCompatActivity() {
    val CHANNEL_ID = "logros"
    val CHANNEL_NAME = "logros"
    private val classificationService = RetrofitInstance.getRetrofit().create(ClassificationService::class.java)

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

        // Obtener nombre de usuario
        val email = DatosUsuario.getEmail(this)


        //Obtener datos de sqlite
        val db = BDsqlite(this)
        val PasosHoy = db.getIntData(email,BDsqlite.COLUMN_PASOS_HOY)
        val PasosTotales = db.getIntData(email,BDsqlite.COLUMN_PASOS_TOTALES)
        val Distancia = db.getFloatData(email,BDsqlite.COLUMN_DISTANCIA)

        //mostrar los datos de cada consulta
        txtPasos.text = (PasosHoy.toString()+" pasos")
        txtPasosT.text = (PasosTotales.toString()+" pasos")
        txtDistancia.text = (Distancia.toString()+ " metros")

        //Procesar clasificacion
        clasificacion(PasosTotales, email)

        //Procesar logros
        consultarlogro(PasosTotales, email)
    }

    private fun clasificacion(pasosT: Int, email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Obtén la clasificación actual por correo electrónico
                val response = classificationService.getClassificationById(email)
                if (response.isSuccessful && response.body() != null) {
                    val clasificacionActual = response.body()!!.data

                    // Comprueba si la clasificación actual no es nula
                    if (clasificacionActual != null) {
                        // Crea una nueva instancia con los datos actualizados
                        val clasificacionActualizada = clasificacionActual.copy(pasos = pasosT+1)

                        // Envía la actualización
                        val updateResponse = classificationService.updateClassification(clasificacionActualizada)
                        if (updateResponse.isSuccessful) {
                            // Manejo exitoso
                            Log.d("Clasificacion Actualizada", "Clasificación actualizada con éxito")
                        } else {
                            // Manejo de errores
                            Log.e("API Error", "Error al actualizar clasificación")
                        }
                    } else {
                        // Manejo del caso en que la clasificación no se encuentra
                        Log.e("API Error", "Clasificación no encontrada para el correo electrónico: $email")
                    }
                } else {
                    // Manejo de errores
                    Log.e("API Error", "Error al obtener clasificación por correo electrónico")
                }
            } catch (e: Exception) {
                // Manejo de excepciones
                Log.e("API Error", "Excepción al actualizar clasificación", e)
            }
        }
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

    private fun consultarlogro(pasosT: Int, email: String) {
        //val pasosT = 101
        // Iniciamos una coroutine en el scope del Fragment
        lifecycleScope.launch {
            try {
                // Creación única de la instancia del servicio para realizar llamadas a la API
                val achievementsService = RetrofitInstance.getRetrofit().create(AchievementService::class.java)

                // Llamada a la API para obtener todos los logros disponibles
                achievementsService.getAchievements().body()?.data?.let { achievements ->
                    // Filtramos los logros basándonos en los pasos realizados por el usuario
                    achievements.filter { pasosT > it.steps }.forEach { achievement ->
                        // Para cada logro que cumpla la condición, verificamos si el usuario ya lo tiene
                        val userAchievementsResponse = achievementsService.getAchievementsByUser(email)
                        userAchievementsResponse.body()?.data?.let { userAchievements ->
                            if (userAchievements.none { it.id == achievement.id }) {
                                // Si el usuario no tiene este logro, intentamos añadir la relación usuario-logro
                                achievementsService.addUserRelation(email, achievement.id!!).body()?.let { response ->
                                    if (response.data == true) {
                                        // Notificación de éxito si se añade el logro correctamente
                                        notificacion(achievement.steps, achievement.name, achievement.id)
                                    } else {
                                        Log.d("FC", "No se pudo añadir la relación usuario-logro para el logro ${achievement.name}")
                                    }
                                }
                            } else {
                                // Manejo del caso en el que el usuario ya tiene asignado el logro
                                Log.d("FC consultarlogro", "El usuario ya tiene el logro ${achievement.name}")
                            }
                        }

                    }
                } ?: Log.e("FC consultarlogro", "No se recibieron logros de la API")
            } catch (e: Exception) {
                // Manejamos cualquier excepción que pueda ocurrir durante el proceso
                Log.e("FC consultarlogro", "Error al consultar logros", e)
            }
        }
    }

    private fun notificacion(pasosLogro: Int, tituloLogro: String, id: Int) {
        Log.d("FC notificacion", "Datos a registrar: $tituloLogro, pasos: $pasosLogro")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Crear un intent que se abrirá al hacer clic en la notificación
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("fragmentoDestino", "LogrosFragment")
            }

            // Crear el PendingIntent
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val notificacion = NotificationCompat.Builder(this, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.logro)
                setContentTitle(tituloLogro)
                setContentText("!Felicidades! obtuviste un logro por haber realizado un total de $pasosLogro pasos")
                priority = NotificationCompat.PRIORITY_DEFAULT
                setContentIntent(pendingIntent) // Establecer el PendingIntent
                setAutoCancel(true) // La notificación se cancela automáticamente al hacer clic en ella
            }.build()

            NotificationManagerCompat.from(this).notify(id, notificacion)
        }
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