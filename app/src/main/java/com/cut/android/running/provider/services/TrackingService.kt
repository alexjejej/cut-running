package com.cut.android.running.provider.services

import MapsViewModel
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cut.android.running.Carreras.TimeUpdateListener
import com.cut.android.running.R
import com.cut.android.running.usecases.home.HomeActivity

class TrackingService : Service() {
    private var viewModel: MapsViewModel? = null
    private var isTimerRunning = false
    private var timerHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        // Inicia el temporizador solo si el intent no es nulo
        intent?.let {
            viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MapsViewModel::class.java)
            startTimer()
            viewModel?.setTimeUpdateListener(object : TimeUpdateListener {
                override fun updateTime(time: String) {
                    // Enviar actualizaciones de tiempo al fragmento
                    val intent = Intent("com.example.trackingapp.TIME_UPDATE")
                    intent.putExtra("time", time)
                    LocalBroadcastManager.getInstance(this@TrackingService).sendBroadcast(intent)
                }
            })
        }
        // Inicia el seguimiento aquí
        return START_STICKY
    }


    private val timeUpdateTask = object : Runnable {
        override fun run() {
            val totalSeconds = (SystemClock.uptimeMillis() - startTime) / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            val timeString = when {
                hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
                else -> String.format("%02d:%02d", minutes, seconds)
            }

            // Envía el tiempo actualizado a la actividad o fragmento
            val intent = Intent("com.example.trackingapp.TIME_UPDATE")
            intent.putExtra("time", timeString)
            LocalBroadcastManager.getInstance(this@TrackingService).sendBroadcast(intent)

            timerHandler.postDelayed(this, 1000)
        }
    }

    private fun startTimer() {
        if (!isTimerRunning) {
            // Inicia el temporizador
            startTime = SystemClock.uptimeMillis()
            timerHandler.postDelayed(timeUpdateTask, 0)
            isTimerRunning = true
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timeUpdateTask)
        isTimerRunning = false
    }

    /**
     * Convierte el tiempo transcurrido en formato String (HH:mm:ss o mm:ss) a milisegundos.
     */
    private fun parseElapsedTime(timeString: String): Long {
        val parts = timeString.split(":").map { it.toIntOrNull() ?: 0 }
        val seconds = when (parts.size) {
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // HH:mm:ss
            2 -> parts[0] * 60 + parts[1] // mm:ss
            else -> 0
        }
        return seconds * 1000L
    }


    private fun buildNotification(): Notification {
        val notificationChannelId = "tracking_channel_id"
        // Crea el intent que inicia HomeActivity
        val intent = Intent(this, HomeActivity::class.java).apply {
            // Asegúrate de limpiar la pila de actividades para que no se cree una nueva instancia de la actividad si ya está corriendo
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Agrega los extras que HomeActivity necesita para saber que debe abrir MapsFragment
            putExtra("fragmentoDestino", "MapsFragment")
            putExtra("lanzadoDesdeNotificacion", true) // Añadir este extra
        }

        // Crea el PendingIntent
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChannelId, "Tracking", NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.principal)
            .setContentTitle("Seguimiento Activo")
            .setContentText("Seguimos registrando tus pasos y distancia :)")
            .setContentIntent(pendingIntent) // Establece el PendingIntent
            .setAutoCancel(true) // La notificación se cancela automáticamente al tocarla

        return builder.build()
    }


}
