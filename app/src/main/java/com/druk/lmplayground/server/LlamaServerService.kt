package com.druk.lmplayground.server

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.druk.lmplayground.App
import com.druk.lmplayground.MainActivity
import com.druk.lmplayground.R
import com.druk.lmplayground.storage.StoragePreferences
import com.druk.lmplayground.util.NetworkUtils

class LlamaServerService : Service() {

    private var server: LlamaServer? = null

    companion object {
        private const val CHANNEL_ID = "llama_server_channel"
        private const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, LlamaServerService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, LlamaServerService::class.java)
            context.stopService(intent)
        }

        fun updateStatus(context: Context) {
            val prefs = StoragePreferences(context)
            val app = context.applicationContext as App
            val shouldRun = prefs.isServerEnabled || app.currentModel != null
            if (shouldRun) {
                start(context)
            } else {
                stop(context)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prefs = StoragePreferences(this)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())

        if (prefs.isServerEnabled) {
            if (server == null) {
                server = LlamaServer(this) {
                    (application as? App)?.currentModel
                }
                server?.start()
            }
        } else {
            server?.stop()
            server = null
        }
        return START_STICKY
    }

    override fun onDestroy() {
        server?.stop()
        server = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val name = "Llama Server"
        val descriptionText = "Running local LLM server"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }

        val prefs = StoragePreferences(this)
        val app = applicationContext as App
        val isServerEnabled = prefs.isServerEnabled
        val isModelLoaded = app.currentModel != null

        val title = if (isServerEnabled) "Llama Server Running" else "LM Playground Active"
        val content = if (isServerEnabled) {
            val ipAddress = NetworkUtils.getLocalIpAddress() ?: "localhost"
            "Listening on http://$ipAddress:8080/v1"
        } else if (isModelLoaded) {
            "Model is loaded in background"
        } else {
            "App is running in background"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }
}
