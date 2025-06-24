package com.example.walletwiz.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationSummaryWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "walletwiz_summary"

        val channel = NotificationChannel(
            channelId,
            "Summary Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setContentTitle("WalletWiz Summary")
            .setContentText("Here's your summary of expenses.")
            .setSmallIcon(com.example.walletwiz.R.drawable.ic_launcher_foreground)
            .build()
        notificationManager.notify(2, notification)
    }
}