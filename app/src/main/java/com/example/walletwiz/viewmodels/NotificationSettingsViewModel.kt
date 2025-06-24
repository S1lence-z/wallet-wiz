package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.states.NotificationSettingsState
import com.example.walletwiz.events.NotificationSettingsEvent
import com.example.walletwiz.workers.NotificationReminderWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import androidx.work.PeriodicWorkRequestBuilder

class NotificationSettingsViewModel(
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val workManager: WorkManager
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationSettingsState())
    val state get() = _state

    init {
        loadNotificationSettings()
    }

    fun onEvent(event: NotificationSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is NotificationSettingsEvent.SetNotificationsEnabled -> {
                    _state.value = _state.value.copy(notificationsEnabled = event.enabled)
                    notificationSettingsRepository.setNotificationsEnabled(event.enabled)
                }
                is NotificationSettingsEvent.SetDailyReminderEnabled -> {
                    _state.value = _state.value.copy(dailyRemindersEnabled = event.enabled)
                    notificationSettingsRepository.setDailyRemindersEnabled(event.enabled)
                    if (event.enabled) {
                        scheduleDailyReminder()
                    } else {
                        cancelDailyReminder()
                    }
                }
                is NotificationSettingsEvent.SetDailyReminderTime -> {
                    _state.value = _state.value.copy(dailyReminderTime = event.time)
                    notificationSettingsRepository.setReminderTime(event.time)
                }
                is NotificationSettingsEvent.SetSummaryNotificationEnabled -> {
                    _state.value = _state.value.copy(summaryNotificationEnabled = event.enabled)
                    notificationSettingsRepository.setSummaryNotificationsEnabled(event.enabled)
                }
                is NotificationSettingsEvent.SetSummaryNotificationFrequency -> {
                    _state.value = _state.value.copy(
                        summaryNotificationFrequency = event.frequency
                    )
                    notificationSettingsRepository.setSummaryNotificationFrequency(event.frequency)
                }
            }
        }
    }

    // TODO: extract the notification functions to a separate utility class if needed
    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun cancelDailyReminder() {
        workManager.cancelUniqueWork("daily_reminder")
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            combine(
                notificationSettingsRepository.notificationsEnabledFlow,
                notificationSettingsRepository.dailyRemindersEnabledFlow,
                notificationSettingsRepository.reminderTimeFlow,
                notificationSettingsRepository.summaryNotificationsEnabledFlow,
                notificationSettingsRepository.summaryNotificationFrequencyFlow
            ) { newNotificationsEnabled, newDailyRemindersEnabled, newReminderTime, newSummaryNotificationsEnabled, newSummaryNotificationsFrequency ->
                _state.value = NotificationSettingsState(
                    notificationsEnabled = newNotificationsEnabled,
                    dailyRemindersEnabled = newDailyRemindersEnabled,
                    dailyReminderTime = newReminderTime,
                    summaryNotificationEnabled = newSummaryNotificationsEnabled,
                    summaryNotificationFrequency = newSummaryNotificationsFrequency

                )
                if (newDailyRemindersEnabled) {
                    scheduleDailyReminder()
                }
            }.collect()
        }
    }
}