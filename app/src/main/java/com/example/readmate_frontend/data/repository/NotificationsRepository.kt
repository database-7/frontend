package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.NotificationsApi
import com.example.readmate_frontend.data.model.request.FcmTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsRepository @Inject constructor(
    private val api: NotificationsApi
) {
    suspend fun saveToken(fcmToken: String) =
        api.saveToken(FcmTokenRequest(fcmToken))

    suspend fun sendPressure(userId: String) =
        api.sendPressure(userId)
}