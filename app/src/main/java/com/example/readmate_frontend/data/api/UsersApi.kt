package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.EditprofileRequest
import com.example.readmate_frontend.data.model.request.LoginRequest
import com.example.readmate_frontend.data.model.request.RegisterRequest
import com.example.readmate_frontend.data.model.response.users.DeregisterResponse
import com.example.readmate_frontend.data.model.response.users.EditprofileResponse
import com.example.readmate_frontend.data.model.response.users.LoginResponse
import com.example.readmate_frontend.data.model.response.users.ProfileResponse
import com.example.readmate_frontend.data.model.response.users.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UsersApi {
    @POST("api/users/register")
            suspend fun register(@Body request: RegisterRequest): RegisterResponse
    @POST("api/users/login")
            suspend fun login(@Body request: LoginRequest): LoginResponse
    @DELETE("api/users/me")
            suspend fun deregister(): DeregisterResponse

    @GET("api/users/me")
            suspend fun profile(): ProfileResponse

    @PUT("api/users/me")
            suspend fun editprofile(@Body request: EditprofileRequest): EditprofileResponse
}