package com.example.walletwiz.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings")

class NotificationSettingsRepository(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminders_enabled")
        val DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time")
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: false
    }

    val dailyRemindersEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] ?: false
    }

    val reminderTimeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DAILY_REMINDER_TIME] ?: "19:00"
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setDailyRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_TIME] = time
        }
    }
}