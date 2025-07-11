package com.egdcoding.dailydoseofmotivation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build


fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "QUOTE_CHANNEL",
            "Daily Quotes",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Sends a daily motivational quote"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


