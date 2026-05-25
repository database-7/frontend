package com.example.readmate_frontend.data.model.response.users

data class ProfileResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileData?
)

data class ProfileData(
    val userNum: Int,
    val userId: String,
    val userName: String
)
