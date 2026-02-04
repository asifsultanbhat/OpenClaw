package com.clawcontroller.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class TermuxCommandService : Service() {
    
    private var wakeLock: PowerManager.WakeLock? = null
    private var isGatewayRunning = false
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        
        // Acquire wake lock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "ClawController::GatewayWakeLock"
        )
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_GATEWAY -> startGateway()
            ACTION_STOP_GATEWAY -> stopGateway()
        }
        
        return START_STICKY
    }
    
    private fun startGateway() {
        if (isGatewayRunning) return
        
        // Start foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Acquire wake lock
        wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes
        
        // Execute Termux commands via Termux:Tasker
        executeTermuxCommands()
        
        isGatewayRunning = true
    }
    
    private fun stopGateway() {
        if (!isGatewayRunning) return
        
        // Stop gateway in Termux
        sendTermuxCommand("pkill -f 'openclaw gateway'")
        
        // Release wake lock
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        
        isGatewayRunning = false
        stopForeground(true)
        stopSelf()
    }
    
    private fun executeTermuxCommands() {
        // Sequence of commands to start OpenClaw with Android patches
        val commands = arrayOf(
            "termux-chroot &",
            "export TMPDIR=\$PREFIX/tmp",
            "mkdir -p \$TMPDIR",
            "openclaw gateway run"
        )
        
        // Send commands to Termux via Termux:Tasker
        for (command in commands) {
            sendTermuxCommand(command)
        }
        
        // Start monitoring for token
        monitorTermuxOutput()
    }
    
    private fun sendTermuxCommand(command: String) {
        val intent = Intent()
        intent.setClassName("com.termux", "com.termux.app.RunCommandService")
        intent.action = "com.termux.RUN_COMMAND"
        intent.putExtra("com.termux.RUN_COMMAND_PATH", "/data/data/com.termux/files/usr/bin/bash")
        intent.putExtra("com.termux.RUN_COMMAND_ARGUMENTS", arrayOf("-c", command))
        intent.putExtra("com.termux.RUN_COMMAND_BACKGROUND", true)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
    
    private fun monitorTermuxOutput() {
        // In a real implementation, this would use Termux:API to read output
        // For now, we'll simulate token detection
        // You would need to implement proper output streaming
    }
    
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_status_online)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }
    
    companion object {
        const val ACTION_START_GATEWAY = "com.clawcontroller.app.START_GATEWAY"
        const val ACTION_STOP_GATEWAY = "com.clawcontroller.app.STOP_GATEWAY"
        private const val CHANNEL_ID = "openclaw_gateway_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
