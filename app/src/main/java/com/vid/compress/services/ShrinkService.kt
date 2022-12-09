package com.vid.compress.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build

import android.os.IBinder
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.config.StorageConfiguration
//import com.iceteck.silicompressorr.SiliCompressor

import com.vid.compress.R
import com.vid.compress.storage.Disk
import com.vid.compress.ui.models.VideoCompressModel

import java.io.File
import java.util.*
import kotlin.math.min


class ShrinkService: Service() {

    private val ID=0x32b65
    private val buttonFilter = IntentFilter()
    private lateinit var  notificationManager:NotificationManager
    private val timer=Timer()
    private var isCompressing=false
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        DataBridge.active=false
        unregisterReceiver(buttonReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        DataBridge.active=true
        return START_NOT_STICKY
    }

    private val buttonReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                else
                    notificationManager.cancel(ID)

               stopSelf()
               VideoCompressor.cancel()
               DataBridge.clear()
            }
        }


    private fun compressFiles(small:RemoteViews,big:RemoteViews,notification:Notification){

        timer.scheduleAtFixedRate(object :TimerTask() {
            override fun run() {
                if(!isCompressing&&!DataBridge.isEmpty()){
                    startVideoCompressionTask(DataBridge.peek(),timer)
                }
                val completed=(DataBridge.originalSize()-DataBridge.currentSize())
                val overall=if(!isCompressing) 100 else (DataBridge.percent.toFloat()).toInt()
                small.setTextViewText(R.id.title, "Compressing...("+completed+"/"+DataBridge.originalSize()+")")
                big.setTextViewText(R.id.title, "Compressing...("+completed+"/"+DataBridge.originalSize()+")")
                small.setProgressBar(R.id.progress, 100, overall, false)
                big.setProgressBar(R.id.progress, 100, overall, false)
                small.setTextViewText(R.id.percent, "($overall%)")
                big.setTextViewText(R.id.percent, "($overall%)")
                small.setTextViewText(R.id.cancel,if(completed==DataBridge.originalSize())"CLOSE" else "CANCEL")
                big.setTextViewText(R.id.cancel,if(completed==DataBridge.originalSize())"CLOSE" else "CANCEL")
           //     if(VideoCompressor.isActive)
                    notificationManager.notify(ID, notification)

            }
        },0,1500)

    }


    @SuppressLint("SuspiciousIndentation")
    private fun startVideoCompressionTask(video:VideoCompressModel, timer: Timer){
        val array= ArrayList<Uri>()
        array.add(Uri.fromFile(File(video.file.filePath)))

        // create app folder in case it doesn't exist
        val file=Disk.createDefaultAppFolder(this)

       VideoCompressor.start(this,array,isStreamable = true, StorageConfiguration(fileName = video.file.fileName,
            saveAt =Disk.getDefaultFolder(),isExternal = true),
           video.getVideoConfiguration(),
            listener = object : CompressionListener {

                override fun onProgress(index: Int, percent: Float) {
                    DataBridge.percent= min ((percent).toInt()+1,100)
                    isCompressing=true
                }
                override fun onStart(index: Int) {
                   isCompressing=true
                }
                override fun onSuccess(index: Int, size: Long, path: String?) {
                    // remove compressed file
                  if(!DataBridge.isEmpty())
                    DataBridge.pop()

                    //compress next file
                    if(!DataBridge.isEmpty()) {
                        startVideoCompressionTask(DataBridge.peek(), timer)
                    }else{
                        //notify user task is complete
                        Toast.makeText(
                            this@ShrinkService,
                            "Compression Finished",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    isCompressing=false
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    Toast.makeText(this@ShrinkService,failureMessage, Toast.LENGTH_SHORT).show()
                   //@debug  println(failureMessage)
                    isCompressing=false

                }

                override fun onCancelled(index: Int) {
                   isCompressing=false
                }
            })
    }

    private fun startNotification(){
        val small=RemoteViews(packageName, R.layout.shrink_notification_small)
        val big=RemoteViews(packageName, R.layout.shrink_notification_big)
        val builder=NotificationCompat.Builder(this,ID.toString())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(small)
            .setCustomBigContentView(big)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        createNotificationChannel()
        // close Button intent
        val closeIntent = Intent()
        closeIntent.action = ID.toString()
        val pIntent = PendingIntent.getBroadcast(this, 0, closeIntent,0)
        big.setOnClickPendingIntent(R.id.cancel, pIntent)
        buttonFilter.addAction(ID.toString())
        registerReceiver(buttonReceiver, buttonFilter)
        val notification=builder.build()
        startForeground(ID,notification)
        compressFiles(small, big,notification)
    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(ID.toString(), "shrinkService", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Shrink Service background task"
             notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }

    }

}