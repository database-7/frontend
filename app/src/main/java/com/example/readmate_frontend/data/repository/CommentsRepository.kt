package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.CommentsApi
import com.example.readmate_frontend.data.model.request.CommentRequest
import com.example.readmate_frontend.data.model.response.posts.CommentItem
import javax.inject.Inject

class CommentsRepository @Inject constructor(
    private val commentsApi: CommentsApi
) {
    suspend fun getComments(groupId: Int, postId: Int): List<CommentItem> =
        commentsApi.getComments(groupId, postId).data.comments

    suspend fun createComment(groupId: Int, postId: Int, content: String) =
        commentsApi.createComment(groupId, postId, CommentRequest(content))

    suspend fun updateComment(groupId: Int, postId: Int, commentId: Int, content: String) =
        commentsApi.updateComment(groupId, postId, commentId, CommentRequest(content))

    suspend fun deleteComment(groupId: Int, postId: Int, commentId: Int) =
        commentsApi.deleteComment(groupId, postId, commentId)
}