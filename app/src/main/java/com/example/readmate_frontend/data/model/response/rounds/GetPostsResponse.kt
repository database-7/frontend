package com.example.readmate_frontend.data.model.response.rounds

data class GetPostsResponse(
    val success: Boolean,
    val message: String?,
    val data: List<PostItem>
)

data class PostItem(
    val postId: Int,
    val userId: Int,
    val userName: String,
    val title: String,
    val content: String,
    val submittedAt: String
)