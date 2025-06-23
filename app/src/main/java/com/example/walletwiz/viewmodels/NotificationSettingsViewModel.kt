package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletwiz.data.NotificationSettingsRepository
import com.example.walletwiz.states.NotificationSettingsState
import com.example.walletwiz.events.NotificationSettingsEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class NotificationSettingsViewModel(
    private val notificationSettingsRepository: NotificationSettingsRepository,
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
                }
                is NotificationSettingsEvent.SetDailyReminderTime -> {
                    _state.value = _state.value.copy(dailyReminderTime = event.time)
                    notificationSettingsRepository.setReminderTime(event.time)
                }
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            combine(
                notificationSettingsRepository.notificationsEnabledFlow,
                notificationSettingsRepository.dailyRemindersEnabledFlow,
                notificationSettingsRepository.reminderTimeFlow
            ) { newNotificationsEnabled, newDailyRemindersEnabled, newReminderTime ->
                _state.value = NotificationSettingsState(
                    notificationsEnabled = newNotificationsEnabled,
                    dailyRemindersEnabled = newDailyRemindersEnabled,
                    dailyReminderTime = newReminderTime
                )
            }.collect()
        }
    }
}