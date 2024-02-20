package com.cut.android.running.provider.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.cut.android.running.R
import com.cut.android.running.usecases.home.HomeActivity

class TrackingService : Service() {


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, buildNotification())
        // Inicia el seguimiento aquí
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
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
