package com.example.pomopet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat

class PomoPetService : Service() {

    private var context: Context? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "100"
    private var isDestroyed = false

    override fun onCreate(){
        super.onCreate()
        context = this
        startForeground(NOTIFICATION_ID, showNotification("This is content."))
    }

    private fun showNotification(content: String): Notification {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID, "PomoPet: Time Remaining",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Time")
            .setContentText(content)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.anim_evol1_red)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Toast.makeText(context, "Starting service...", Toast.LENGTH_SHORT).show()
        val currentTime = intent?.getStringExtra("CURRENT_TIME")
        doTask(currentTime.toString())
        return super.onStartCommand(intent, flags, startId)
    }

    fun doTask(data: String){
        // Keep Getting New Time Update

        updateNotification(data)
    }

    private fun updateNotification(data: String){
        val notification: Notification = showNotification(data)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onDestroy(){
        super.onDestroy()
        isDestroyed = true
        //Toast.makeText(context, "Stoping service...", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null
    }
}