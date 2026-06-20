package com.sum1t.preppy.common.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context) {

    val channel = NotificationChannel(
        "channel_id",
        "General Notifications",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    channel.apply {
        description = "App notifications"
        enableVibration(true)
        enableLights(true)
    }

    val manager =
        context.getSystemService(NotificationManager::class.java)

    manager.createNotificationChannel(channel)
}