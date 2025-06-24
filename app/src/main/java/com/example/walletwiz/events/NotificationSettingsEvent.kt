package com.example.walletwiz.events

import com.example.walletwiz.utils.Frequency

sealed interface NotificationSettingsEvent {
    data class SetNotificationsEnabled(val enabled: Boolean) : NotificationSettingsEvent

    // Daily reminders
    data class SetDailyReminderEnabled(val enabled: Boolean) : NotificationSettingsEvent
    data class SetDailyReminderTime(val time: String) : NotificationSettingsEvent

    // Summary notifications
    data class SetSummaryNotificationEnabled(val enabled: Boolean) : NotificationSettingsEvent
    data class SetSummaryNotificationFrequency(val frequency: Frequency) : NotificationSettingsEvent
}