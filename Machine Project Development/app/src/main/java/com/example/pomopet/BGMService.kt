package com.example.pomopet

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.io.IOException
import kotlin.random.Random

class BGMService : Service() {

    var mediaPlayer : MediaPlayer? = null

    private var context: Context? = null
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "100"
    private var isDestroyed = false

    override fun onCreate() {
        super.onCreate()
        context = this
        startForeground(NOTIFICATION_ID, showNotification("This is content."))
    }

    private fun showNotification(content: String): Notification {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID, "PomoPet: Time Remaining",
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ongoing Pomodoro | Playing Music...")
            .setContentText(content)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setSmallIcon(R.drawable.anim_evol1_red)
            .build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val signalKey = intent?.getStringExtra("SIGNAL_KEY")
        onPlay(signalKey.toString());
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onPlay(signalKey: String){

        if(signalKey == "start"){

            // To avoid a lot of background music being played numerous time every "start"
            // Check if MediaPlayer is currently being executed or not. If not, create
            if (mediaPlayer == null) {
                // Randomly choose between bgm1 and bgm2
                val randomBGM = if (Random.nextBoolean()) R.raw.bgm1 else R.raw.bgm2
                mediaPlayer = MediaPlayer.create(this, randomBGM)
            }

            // If MediaPlayer is not playing anything, start playing
            if ( !mediaPlayer!!.isPlaying){
                try{
                    mediaPlayer!!.start()

                    val sharedPref = getSharedPreferences("FILE_SETTINGS", MODE_PRIVATE)
                    var volumeLevel = sharedPref.getFloat("VOLUME_SETTINGS", 0F)
                    volumeLevel /= 100F
                    mediaPlayer!!.setVolume(volumeLevel, volumeLevel)

                    mediaPlayer!!.setLooping(true)

                    updateNotification("Playing Music...")

                }catch (e: IOException){
                    e.printStackTrace()
                }
            }

        } else if ( signalKey == "pause" ){
            onPause()
        } else if ( signalKey == "skip" ){
            onSkip()
        } else if ( signalKey == "volume"){
            onChangeVolume()
        }

    }

    private fun updateNotification(data: String){
        val notification: Notification = showNotification(data)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun onPause(){
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause() // Pauses the song without resetting
        }
    }

    private fun onSkip(){
        if (mediaPlayer != null && mediaPlayer!!.isPlaying){
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.bgm1)
            mediaPlayer!!.start()
        }
    }

    private fun onChangeVolume(){
        val sharedPref = getSharedPreferences("FILE_SETTINGS", MODE_PRIVATE)

        var volumeLevel = sharedPref.getFloat("VOLUME_SETTINGS", 0F)
        volumeLevel /= 100F

        if (mediaPlayer != null)
            mediaPlayer!!.setVolume(volumeLevel, volumeLevel)
    }

    private fun onFinished(){
        if (mediaPlayer != null ){
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.finished)

        if ( !mediaPlayer!!.isPlaying){
            try{
                mediaPlayer!!.start()
                mediaPlayer!!.setVolume(0.1F, 0.1F)
                updateNotification("Pomodoro Finished | Congratulations!")

            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }

        onFinished()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
        return null
    }

}