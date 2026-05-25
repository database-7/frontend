package com.example.readmate_frontend.data.model.response.posts

data class GetPostDetailResponse(
    val success: Boolean,
    val message: String?,
    val data: PostDetailData
)

data class PostDetailData(
    val postId: Int,
    val title: String,
    val content: String,
    val author: String,
    val submittedAt: String,
    val roundId: Int,
    val roundNumber: Int,
    val startPage: Int,
    val endPage: Int,
    val comments: List<CommentItem>
)

data class CommentItem(
    val commentId: Int,
    val parentCommentId: Int,
    val content: String,
    val author: String,
    val createdAt: String
)

data class CommentsData(val comments: List<CommentItem>)