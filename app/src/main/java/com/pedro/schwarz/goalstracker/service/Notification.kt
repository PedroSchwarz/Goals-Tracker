package com.pedro.schwarz.goalstracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.pedro.schwarz.goalstracker.R

fun showNotification(
    context: Context,
    title: String,
    shortDescription: String,
    description: String
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            "goal_expiring_channel",
            "Goal Expiring Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val builder = NotificationCompat.Builder(context, "goal_expiring_channel")
    builder.apply {
        setContentTitle(title)
        setContentText(shortDescription)
        setStyle(NotificationCompat.BigTextStyle().bigText(description))
        setSmallIcon(R.mipmap.ic_launcher)
        notificationManager.notify(1, this.build())
    }
}