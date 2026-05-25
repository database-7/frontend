package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.example.readmate_frontend.data.local.NotificationStore
import com.example.readmate_frontend.data.model.local.LocalNotificationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val store: NotificationStore
) : ViewModel() {

    val notifications: StateFlow<List<LocalNotificationItem>> = store.notifications

    fun clearAll() = store.clear()
}