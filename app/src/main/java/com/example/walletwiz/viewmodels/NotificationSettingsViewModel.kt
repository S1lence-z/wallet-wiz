package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.events.NotificationSettingsEvent
import com.example.walletwiz.states.NotificationSettingsState
import com.example.walletwiz.utils.DailyReminderUtils
import com.example.walletwiz.utils.SummaryNotificationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import androidx.work.WorkManager

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
                    notificationSettingsRepository.setNotificationsEnabled(event.enabled)
                }
                is NotificationSettingsEvent.SetDailyReminderEnabled -> {
                    notificationSettingsRepository.setDailyRemindersEnabled(event.enabled)
                }
                is NotificationSettingsEvent.SetDailyReminderTime -> {
                    notificationSettingsRepository.setReminderTime(event.time)
                }
                is NotificationSettingsEvent.SetSummaryNotificationEnabled -> {
                    notificationSettingsRepository.setSummaryNotificationsEnabled(event.enabled)
                }
                is NotificationSettingsEvent.SetSummaryNotificationFrequency -> {
                    notificationSettingsRepository.setSummaryNotificationFrequency(event.frequency)
                }
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            // This combine is for updating the UI state
            combine(
                notificationSettingsRepository.notificationsEnabledFlow,
                notificationSettingsRepository.dailyRemindersEnabledFlow,
                notificationSettingsRepository.reminderTimeFlow,
                notificationSettingsRepository.summaryNotificationsEnabledFlow,
                notificationSettingsRepository.summaryNotificationFrequencyFlow
            ) { notificationsEnabled, dailyRemindersEnabled, reminderTime, summaryEnabled, summaryFrequency ->
                NotificationSettingsState(
                    notificationsEnabled = notificationsEnabled,
                    dailyRemindersEnabled = dailyRemindersEnabled,
                    dailyReminderTime = reminderTime,
                    summaryNotificationEnabled = summaryEnabled,
                    summaryNotificationFrequency = summaryFrequency
                )
            }.collect { newState ->
                _state.value = newState
            }
        }

        viewModelScope.launch {
            // This flow handles scheduling for the daily reminder
            combine(
                notificationSettingsRepository.notificationsEnabledFlow,
                notificationSettingsRepository.dailyRemindersEnabledFlow,
                notificationSettingsRepository.reminderTimeFlow
            ) { notificationsEnabled, dailyRemindersEnabled, reminderTime ->
                Triple(notificationsEnabled, dailyRemindersEnabled, reminderTime)
            }.distinctUntilChanged().collect { (notificationsEnabled, dailyRemindersEnabled, reminderTime) ->
                if (notificationsEnabled && dailyRemindersEnabled) {
                    DailyReminderUtils.scheduleDailyReminder(workManager, reminderTime)
                } else {
                    DailyReminderUtils.cancelDailyReminder(workManager)
                }
            }
        }

        viewModelScope.launch {
            // This flow handles scheduling for the summary notification
            combine(
                notificationSettingsRepository.notificationsEnabledFlow,
                notificationSettingsRepository.summaryNotificationsEnabledFlow,
                notificationSettingsRepository.summaryNotificationFrequencyFlow
            ) { notificationsEnabled, summaryEnabled, summaryFrequency ->
                Triple(notificationsEnabled, summaryEnabled, summaryFrequency)
            }.distinctUntilChanged().collect { (notificationsEnabled, summaryEnabled, summaryFrequency) ->
                if (notificationsEnabled && summaryEnabled) {
                    SummaryNotificationUtils.scheduleSummaryNotification(workManager, summaryFrequency)
                } else {
                    SummaryNotificationUtils.cancelSummaryNotification(workManager)
                }
            }
        }
    }
}