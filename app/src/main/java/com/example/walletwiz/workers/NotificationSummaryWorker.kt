package com.example.walletwiz.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class NotificationSummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        sendSummaryNotification()
        return Result.success()
    }

    private fun sendSummaryNotification() {
        Log.d("NotificationSummaryWorker", "Sending summary notification")
    }
}