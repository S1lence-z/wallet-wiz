package com.example.walletwiz.events

sealed interface NotificationSettingsEvent {
    data class SetNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvent
    data class SetDailyReminderEnabled(val enabled: Boolean) : NotificationSettingsEvent
    data class SetDailyReminderTime(val time: String) : NotificationSettingsEvent
}