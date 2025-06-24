package com.example.walletwiz.utils

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.walletwiz.workers.NotificationReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyReminderUtils {
    fun scheduleDailyReminder(workManager: WorkManager, time: String) {
        val (hour, minute) = try {
            time.split(":").map { it.toInt() }
        } catch (e: Exception) {
            listOf(19, 0)
        }

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(now)) {
                add(Calendar.DATE, 1)
            }
        }
        Log.d("DailyReminderUtils", "Scheduling daily reminder for $time at ${target.time}")

        val initialDelay = target.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<NotificationReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
        Log.d("DailyReminderUtils", "Scheduling daily reminder for $time, initial delay: $initialDelay ms")
        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyReminder(workManager: WorkManager) {
        workManager.cancelUniqueWork("daily_reminder")
    }
}