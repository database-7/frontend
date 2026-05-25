package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.FcmTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationsApi {

    @POST("api/notifications/token")
    suspend fun saveToken(
        @Body request: FcmTokenRequest
    ): Response<Unit>

    @POST("api/notifications/pressure/{userId}")
    suspend fun sendPressure(
        @Path("userId") userId: String
    ): Response<Unit>
}