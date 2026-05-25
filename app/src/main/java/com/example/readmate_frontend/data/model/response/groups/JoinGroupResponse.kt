package com.example.readmate_frontend.data.model.response.groups

data class JoinGroupResponse(
    val success: Boolean,
    val message: String,
    val data: JoinGroupData
)

data class JoinGroupData(
    val groupId: Int,
    val name: String
)
