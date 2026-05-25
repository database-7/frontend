package com.example.readmate_frontend.service

import android.util.Log
import com.example.readmate_frontend.data.local.NotificationStore
import com.example.readmate_frontend.data.model.local.LocalNotificationItem
import com.example.readmate_frontend.data.repository.NotificationsRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ReadMateFcmService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: NotificationsRepository

    @Inject
    lateinit var notificationStore: NotificationStore

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(Dispatchers.IO).launch {
            try { repository.saveToken(token) } catch (e: Exception) { }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM", "onMessageReceived! data=${message.data}, notification=${message.notification}")

        val title = message.data["title"] ?: message.notification?.title ?: "ReadMate"
        val body = message.data["body"] ?: message.notification?.body ?: ""
        val now = System.currentTimeMillis()

        notificationStore.add(
            LocalNotificationItem(
                title = title,
                body = body,
                receivedAt = now
            )
        )
    }
}