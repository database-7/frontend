package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.CreateGroupRequest
import com.example.readmate_frontend.data.model.request.JoinGroupRequest
import com.example.readmate_frontend.data.model.request.UpdateGroupRequest
import com.example.readmate_frontend.data.model.response.groups.CreateGroupResponse
import com.example.readmate_frontend.data.model.response.groups.GetMembersResponse
import com.example.readmate_frontend.data.model.response.groups.GroupHomeResponse
import com.example.readmate_frontend.data.model.response.groups.GroupListResponse
import com.example.readmate_frontend.data.model.response.groups.InviteCodeResponse
import com.example.readmate_frontend.data.model.response.groups.JoinGroupResponse
import com.example.readmate_frontend.data.model.response.groups.UpdateGroupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GroupsApi {
    @GET("api/groups")
    suspend fun getGroups(): GroupListResponse

    @PUT("api/groups/{groupId}")
    suspend fun updateGroup(
        @Path("groupId") groupId: Int,
        @Body request: UpdateGroupRequest
    ): UpdateGroupResponse

    @DELETE("api/groups/{groupId}")
    suspend fun deleteGroup(
        @Path("groupId") groupId: Int
    ): Response<Unit>

    @GET("api/groups/{groupId}")
    suspend fun getGroupHome(@Path("groupId") groupId: Int): GroupHomeResponse

    @POST("api/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): CreateGroupResponse

    @POST("api/groups/join")
    suspend fun joinGroup(
        @Body request: JoinGroupRequest
    ): JoinGroupResponse

    @GET("api/groups/{groupId}/members")
    suspend fun getGroupMembers(
        @Path("groupId") groupId: Int
    ): GetMembersResponse

    @DELETE("api/groups/{groupId}/members")
    suspend fun leaveGroup(
        @Path("groupId") groupId: Int
    ): Response<Unit>

    @GET("api/groups/{groupId}/invite-code")
    suspend fun getInviteCode(
        @Path("groupId") groupId: Int
    ): InviteCodeResponse

}
