package com.example.readmate_frontend.data.model.response.groups

data class GetMembersResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, List<GroupMemberItem>>
)

enum class MemberRole {
    OWNER, MEMBER
}
data class GroupMemberItem(
    val userNum: Int,
    val userName: String,
    val userId: String,
    val role: MemberRole,
    val joinedAt: String
)
