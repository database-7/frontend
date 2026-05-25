package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.CommentRequest
import com.example.readmate_frontend.data.model.response.books.ApiResponse
import com.example.readmate_frontend.data.model.response.posts.CommentsData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CommentsApi {
    @GET("api/groups/{groupId}/posts/{postId}/comments")
    suspend fun getComments(
        @Path("groupId") groupId: Int,
        @Path("postId") postId: Int
    ): ApiResponse<CommentsData>

    @POST("api/groups/{groupId}/posts/{postId}/comments")
    suspend fun createComment(
        @Path("groupId") groupId: Int,
        @Path("postId") postId: Int,
        @Body request: CommentRequest
    ): Response<Unit>

    @PUT("api/groups/{groupId}/posts/{postId}/comments/{commentId}")
    suspend fun updateComment(
        @Path("groupId") groupId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Body request: CommentRequest
    ): Response<Unit>

    @DELETE("api/groups/{groupId}/posts/{postId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("groupId") groupId: Int,
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int
    ): Response<Unit>
}