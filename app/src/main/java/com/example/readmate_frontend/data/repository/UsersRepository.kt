package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.model.request.EditprofileRequest
import com.example.readmate_frontend.data.model.request.LoginRequest
import com.example.readmate_frontend.data.model.request.RegisterRequest
import com.example.readmate_frontend.data.model.response.users.DeregisterResponse
import com.example.readmate_frontend.data.model.response.users.EditprofileResponse
import com.example.readmate_frontend.data.model.response.users.LoginResponse
import com.example.readmate_frontend.data.model.response.users.ProfileResponse
import com.example.readmate_frontend.data.model.response.users.RegisterResponse
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val api: UsersApi){
    suspend fun register(
        userId: String,
        password: String,
        userName: String
    ): RegisterResponse {
        return api.register(RegisterRequest(userId, password, userName))
    }

    suspend fun login(
        userId: String,
        password: String
    ): LoginResponse {
        return api.login(LoginRequest(userId, password))
    }

    suspend fun deregister(): DeregisterResponse {
        return api.deregister()
    }

    suspend fun profile(): ProfileResponse{
        return api.profile()
    }

    suspend fun  editprofile(
        userId: String,
        password: String,
        userName: String
    ): EditprofileResponse{
        return api.editprofile(EditprofileRequest(userId, password, userName))
    }
}