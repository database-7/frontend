package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.response.posts.GetPostDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface PostsApi{

    @GET("api/groups/{groupId}/posts/{postId}")
    suspend fun getPostDetail(
        @Path("groupId") groupId: Int,
        @Path("postId") postId: Int
    ): GetPostDetailResponse
}
