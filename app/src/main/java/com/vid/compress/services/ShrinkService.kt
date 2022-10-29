package com.vid.compress.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.vid.compress.R


class ShrinkService: Service() {

    private val ID="0x32b65"
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        super.onCreate()
    }


    override fun onDestroy() {
        super.onDestroy()
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    private fun startNotification(){
        val layout1=RemoteViews(packageName, R.layout.shrink_notification_small)
        val layout2=RemoteViews(packageName, R.layout.shrink_notification_big)
        val builder=NotificationCompat.Builder(this,ID)
          //  .setSmallIcon(R.drawable.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(layout1)
            .setCustomBigContentView(layout2)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(ID, "shrinkService", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Shrink Service background task"
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

    }

}