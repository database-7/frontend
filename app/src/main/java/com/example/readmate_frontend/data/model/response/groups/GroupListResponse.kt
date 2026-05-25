package com.example.readmate_frontend.data.model.response.groups

data class GroupListResponse(
    val success: Boolean,
    val message: String?,
    val data: GroupListData
)

data class GroupListData(
    val groups: List<GroupItem>
)

data class GroupItem(
    val groupId: Int,
    val name: String,
    val inviteCode: String
)
