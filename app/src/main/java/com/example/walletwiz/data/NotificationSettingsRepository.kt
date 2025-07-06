package com.example.walletwiz.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.walletwiz.utils.Frequency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings")

class NotificationSettingsRepository(context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminders_enabled")
        val DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time")
        val SUMMARY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("summary_notifications_enabled")
        val SUMMARY_NOTIFICATION_FREQUENCY = stringPreferencesKey("summary_notification_frequency")
    }

    val notificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] == true
    }

    val dailyRemindersEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] == true
    }

    val reminderTimeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DAILY_REMINDER_TIME] ?: "19:00"
    }

    val summaryNotificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SUMMARY_NOTIFICATIONS_ENABLED] == true
    }

    val summaryNotificationFrequencyFlow: Flow<Frequency> = dataStore.data.map { preferences ->
        val frequencyString = preferences[PreferencesKeys.SUMMARY_NOTIFICATION_FREQUENCY] ?: Frequency.DAILY.name
        Frequency.valueOf(frequencyString)
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
            }
        }
    }

    suspend fun setDailyRemindersEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DAILY_REMINDER_ENABLED] = enabled
            }
        }
    }

    suspend fun setReminderTime(time: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.DAILY_REMINDER_TIME] = time
            }
        }
    }

    suspend fun setSummaryNotificationsEnabled(enabled: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SUMMARY_NOTIFICATIONS_ENABLED] = enabled
            }
        }
    }

    suspend fun setSummaryNotificationFrequency(frequency: Frequency) {
        withContext(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SUMMARY_NOTIFICATION_FREQUENCY] = frequency.name
            }
        }
    }
}