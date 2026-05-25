package com.example.readmate_frontend.data.local

import android.content.Context
import com.example.readmate_frontend.data.model.local.LocalNotificationItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)
    private val gson = com.google.gson.Gson()

    private val _notifications = MutableStateFlow<List<LocalNotificationItem>>(load())
    val notifications: StateFlow<List<LocalNotificationItem>> = _notifications.asStateFlow()

    fun add(item: LocalNotificationItem) {
        val updated = listOf(item) + _notifications.value
        _notifications.value = updated
        save(updated)
    }

    fun clear() {
        _notifications.value = emptyList()
        prefs.edit().remove("list").apply()
    }

    private fun save(list: List<LocalNotificationItem>) {
        prefs.edit().putString("list", gson.toJson(list)).apply()
    }

    private fun load(): List<LocalNotificationItem> {
        val json = prefs.getString("list", null) ?: return emptyList()
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<LocalNotificationItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) { emptyList() }
    }
}