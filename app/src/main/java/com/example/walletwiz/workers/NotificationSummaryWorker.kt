package com.example.walletwiz.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.walletwiz.data.database.AppDatabase
import com.example.walletwiz.utils.Currency
import com.example.walletwiz.utils.Frequency
import com.example.walletwiz.utils.formatCurrency

class NotificationSummaryWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val frequencyString = inputData.getString("frequency") ?: "WEEKLY"
            val frequency = Frequency.fromString(frequencyString) ?: Frequency.WEEKLY
            sendNotification(frequency)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun sendNotification(frequency: Frequency) {
        val database = AppDatabase(context)
        val expenseDao = database.expenseDao()

        val (total, count, period) = when (frequency) {
            Frequency.DAILY -> {
                Triple(
                    expenseDao.getTodayTotal(),
                    expenseDao.getTodayExpenseCount(),
                    "today"
                )
            }
            Frequency.WEEKLY -> {
                Triple(
                    expenseDao.getWeekTotal(),
                    expenseDao.getWeekExpenseCount(),
                    "this week"
                )
            }
            Frequency.MONTHLY -> {
                Triple(
                    expenseDao.getMonthTotal(),
                    expenseDao.getMonthExpenseCount(),
                    "this month"
                )
            }
        }

        val contentText = constructNotificationMessage(total, count, period)
        val titleText = getNotificationTitle(frequency)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "wallet_summary"

        val channel = NotificationChannel(
            channelId,
            "Summary Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Periodic expense summaries"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setContentTitle(titleText)
            .setContentText(contentText)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(contentText))
            .setSmallIcon(com.example.walletwiz.R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun constructNotificationMessage(total: Double, count: Int, period: String): String {
        val formattedAmount = formatCurrency(total, Currency.CZK)
        val expenseText = if (count == 1) "expense" else "expenses"

        return if (count > 0) {
            "You spent $formattedAmount on $count $expenseText $period."
        } else {
            "No expenses recorded $period. Great job staying on budget!"
        }
    }

    private fun getNotificationTitle(frequency: Frequency): String {
        return "WalletWiz ${frequency.name.lowercase().replaceFirstChar { it.uppercase() }} Summary"
    }
}