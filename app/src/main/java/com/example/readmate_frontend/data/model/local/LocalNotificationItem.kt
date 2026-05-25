package com.example.readmate_frontend.data.model.local

data class LocalNotificationItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val body: String,
    val receivedAt: Long = System.currentTimeMillis()
)
