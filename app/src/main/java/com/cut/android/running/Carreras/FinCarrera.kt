package com.cut.android.running.Carreras

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.models.Achievement
import com.cut.android.running.models.Classification
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.cut.android.running.usecases.home.HomeActivity
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.AchievementService
import com.cut.android.running.provider.services.ClassificationService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class FinCarrera : AppCompatActivity() {
    val CHANNEL_ID = "logros"
    val CHANNEL_NAME = "logros"
    private val classificationService = RetrofitInstance.getRetrofit().create(ClassificationService::class.java)
    private val achievementsService: AchievementService by lazy {
        RetrofitInstance.getRetrofit().create(AchievementService::class.java)
    }

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
        val nombreUsuario = DatosUsuario.getUserName(this)

        //mostrar los datos de cada consulta
        txtPasos.text = (PasosHoy.toString()+" pasos")
        txtPasosT.text = (PasosTotales.toString()+" pasos")
        txtDistancia.text = (Distancia.toString()+ " metros")

        //Procesar clasificacion
        if (nombreUsuario != null) {
            clasificacion(PasosTotales, email, nombreUsuario)
        }

        //Procesar logros
        consultarlogro(PasosTotales, email)
    }

    private fun clasificacion(pasosT: Int, email: String, nombreUsuario: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Intenta obtener la clasificación actual por correo electrónico
                val response = classificationService.getClassificationById(email)
                if (response.isSuccessful && response.body() != null && response.body()!!.isSuccess) {
                    val clasificacionActual = response.body()!!.data

                    if (clasificacionActual != null) {
                        // Si existe, actualiza la clasificación
                        val clasificacionActualizada = clasificacionActual.copy(pasos = pasosT + 1)
                        val updateResponse = classificationService.updateClassification(clasificacionActualizada)
                        if (updateResponse.isSuccessful) {
                            Log.d("Clasificacion Actualizada", "Clasificación actualizada con éxito")
                        } else {
                            Log.e("API Error", "Error al actualizar clasificación")
                        }
                    }
                } else if (response.code() == 404) {
                    // Si no existe la clasificación, crea una nueva
                    val nuevaClasificacion = Classification(id = email, nombre = nombreUsuario, pasos = pasosT) // Asume la existencia de un constructor adecuado
                    val createResponse = classificationService.addClassification(nuevaClasificacion)
                    if (createResponse.isSuccessful) {
                        Log.d("Clasificacion Creada", "Clasificación creada con éxito")
                    } else {
                        Log.e("API Error", "Error al crear clasificación")
                    }
                } else {
                    // Otros errores
                    Log.e("API Error", "Error al obtener clasificación por correo electrónico: $response")
                }
            } catch (e: Exception) {
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

    // Asume que tus métodos de API ya son suspendidos
    private suspend fun getAchievementsByEmail(email: String): List<Achievement>? {
        val response = achievementsService.getAchievementsByUser(email)
        return if (response.isSuccessful) response.body()?.data else null
    }

    private suspend fun addAchievementIfNotExists(pasos: Int, email: String, achievements: List<Achievement>, userAchievements: List<Achievement>?) {
        achievements.filter { pasos > it.steps }.forEach { achievement ->
            if (userAchievements?.none { it.id == achievement.id } != false) {
                val response = achievementsService.addUserRelation(email, achievement.id!!)
                if (response.isSuccessful && response.body()?.data == true) {
                    notificacion(achievement.steps, achievement.name, achievement.id)
                } else {
                    Log.d("FC consultarlogro", "No se pudo añadir la relación usuario-logro para el logro ${achievement.name}")
                }
            } else {
                Log.d("FC consultarlogro", "El usuario ya tiene el logro ${achievement.name}")
            }
        }
    }

    private fun consultarlogro(pasos: Int, email: String) {
        lifecycleScope.launch(CoroutineExceptionHandler { _, exception ->
            Log.e("FC consultarlogro", "Error en la coroutine", exception)
        }) {
            try {
                val achievements = achievementsService.getAchievements().body()?.data ?: emptyList()
                val userAchievements = getAchievementsByEmail(email)
                addAchievementIfNotExists(pasos, email, achievements, userAchievements)
            } catch (e: Exception) {
                Log.e("FC consultarlogro", "Error al consultar logros", e)
            }
        }
    }


    private fun notificacion(pasosLogro: Int, tituloLogro: String, id: Int) {
        Log.d("FC consultarlogro", "Datos a registrar: $tituloLogro, pasos: $pasosLogro")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            val manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Crear un intent que se abrirá al hacer clic en la notificación
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("fragmentoDestino", "LogroConseguidoFragment")
                putExtra("nombreLogro", tituloLogro)
                putExtra("pasosLogro", pasosLogro)
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

            if (ActivityCompat.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
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