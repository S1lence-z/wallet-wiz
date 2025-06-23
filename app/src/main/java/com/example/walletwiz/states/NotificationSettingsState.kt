package com.example.walletwiz.states

data class NotificationSettingsState(
    val notificationsEnabled: Boolean = false,
    val dailyRemindersEnabled: Boolean = false,
    val dailyReminderTime: String = "19:00" // TODO: Saved as HH:mm for now, use a more suitable type later
)