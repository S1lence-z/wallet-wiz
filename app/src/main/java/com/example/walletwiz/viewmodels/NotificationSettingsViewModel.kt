package com.example.walletwiz.viewmodels

import androidx.lifecycle.ViewModel
import com.example.walletwiz.states.NotificationSettingsState
import kotlinx.coroutines.flow.MutableStateFlow

class NotificationSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(NotificationSettingsState())
    val state get() = _state
}