package com.cut.android.running.provider.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.cut.android.running.R

class TrackingService : Service() {

    private val repository = TrackingRepository()

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(notificationChannelId, "Tracking", NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, notificationChannelId)
            .setOngoing(true)
            .setSmallIcon(R.drawable.principal)
            .setContentTitle("Seguimiento Activo")
            .setContentText("Seguimos registrando tus pasos y distancia :)")
        // Considera agregar un PendingIntent para abrir la app al tocar la notificación

        return builder.build()
    }

    private fun updateTrackingInfo(isTracking: Boolean, steps: Int, distance: Float) {
        repository.updateTracking(isTracking)
        repository.updateSteps(steps)
        repository.updateDistance(distance)
    }
}
