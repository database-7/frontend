package com.example.readmate_frontend.data.model.response.groups

data class CreateGroupResponse(
    val success: Boolean,
    val message: String,
    val data: GroupData
)

data class GroupData(
    val groupId: Int,
    val name: String,
    val inviteCode: String
)
