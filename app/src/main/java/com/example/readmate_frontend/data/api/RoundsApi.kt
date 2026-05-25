package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.CreatePostRequest
import com.example.readmate_frontend.data.model.response.posts.GetPostDetailResponse
import com.example.readmate_frontend.data.model.response.rounds.GetPostsResponse
import com.example.readmate_frontend.data.model.response.rounds.GetRoundsResponse
import com.example.readmate_frontend.data.model.response.rounds.MyStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoundsApi {

    @GET("api/groups/{groupId}/categories/{categoryId}/rounds")
    suspend fun getRounds(
        @Path("groupId") groupId: Int,
        @Path("categoryId") categoryId: Int
    ): GetRoundsResponse

    @GET("api/groups/{groupId}/categories/{categoryId}/rounds/{roundId}/posts")
    suspend fun getPosts(
        @Path("groupId") groupId: Int,
        @Path("categoryId") categoryId: Int,
        @Path("roundId") roundId: Int
    ): GetPostsResponse

    @POST("api/groups/{groupId}/categories/{categoryId}/rounds/{roundId}/posts")
    suspend fun createPost(
        @Path("groupId") groupId: Int,
        @Path("categoryId") categoryId: Int,
        @Path("roundId") roundId: Int,
        @Body request: CreatePostRequest
    ): retrofit2.Response<Unit>

    @GET("api/groups/{groupId}/categories/{categoryId}/rounds/{roundId}/my-status")
    suspend fun getMyStatus(
        @Path("groupId") groupId: Int,
        @Path("categoryId") categoryId: Int,
        @Path("roundId") roundId: Int
    ): MyStatusResponse

}