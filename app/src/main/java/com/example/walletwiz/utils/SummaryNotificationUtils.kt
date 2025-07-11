package com.example.walletwiz.utils

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.walletwiz.workers.NotificationSummaryWorker
import java.util.concurrent.TimeUnit

object SummaryNotificationUtils {

    fun scheduleSummaryNotification(workManager: WorkManager, frequency: Frequency) {
        val repeatInterval = when (frequency) {
            Frequency.DAILY -> 1L
            Frequency.WEEKLY -> 7L
            Frequency.MONTHLY -> 30L
        }
        val timeUnit = TimeUnit.DAYS

        val inputData = Data.Builder()
            .putString("frequency", frequency.name)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationSummaryWorker>(
            repeatInterval, timeUnit
        )
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "summary_notification",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelSummaryNotification(workManager: WorkManager) {
        workManager.cancelUniqueWork("summary_notification")
    }
}