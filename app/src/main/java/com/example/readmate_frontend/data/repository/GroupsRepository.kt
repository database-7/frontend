package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.GroupsApi
import com.example.readmate_frontend.data.model.request.CreateGroupRequest
import com.example.readmate_frontend.data.model.request.JoinGroupRequest
import com.example.readmate_frontend.data.model.request.UpdateGroupRequest
import com.example.readmate_frontend.data.model.response.groups.CreateGroupResponse
import com.example.readmate_frontend.data.model.response.groups.GroupHomeData
import com.example.readmate_frontend.data.model.response.groups.GroupItem
import com.example.readmate_frontend.data.model.response.groups.GroupMemberItem
import javax.inject.Inject

class GroupsRepository @Inject constructor(
    private val groupsApi: GroupsApi
) {
    suspend fun createGroup(name: String): CreateGroupResponse =
        groupsApi.createGroup(CreateGroupRequest(name = name, photoUrl = ""))

    suspend fun getGroups(): List<GroupItem> =
        groupsApi.getGroups().data.groups

    suspend fun getGroupHome(groupId: Int): GroupHomeData =
        groupsApi.getGroupHome(groupId).data

    suspend fun updateGroup(groupId: Int, newName: String) {
        groupsApi.updateGroup(groupId, UpdateGroupRequest(name = newName))
    }
    suspend fun deleteGroup(groupId: Int) =
        groupsApi.deleteGroup(groupId)

    suspend fun joinGroup(inviteCode: String) =
        groupsApi.joinGroup(JoinGroupRequest(inviteCode = inviteCode))

    suspend fun getGroupMembers(groupId: Int): List<GroupMemberItem> =
        groupsApi.getGroupMembers(groupId).data.values.flatten()

    suspend fun leaveGroup(groupId: Int) =
        groupsApi.leaveGroup(groupId)

    suspend fun getInviteCode(groupId: Int): Map<String, String> =
        groupsApi.getInviteCode(groupId).data
}