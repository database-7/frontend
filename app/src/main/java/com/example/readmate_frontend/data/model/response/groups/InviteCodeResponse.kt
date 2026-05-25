package com.example.readmate_frontend.data.model.response.groups

data class InviteCodeResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, String>
)
