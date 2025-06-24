package com.example.walletwiz.states

import com.example.walletwiz.utils.Frequency

data class NotificationSettingsState(
    val notificationsEnabled: Boolean = false,

    // Daily reminders
    val dailyRemindersEnabled: Boolean = false,
    val dailyReminderTime: String = "19:00", // TODO: Saved as HH:mm for now, use a more suitable type later

    // Summary notifications
    val summaryNotificationEnabled: Boolean = false,
    val summaryNotificationFrequency: Frequency = Frequency.MONTHLY
)