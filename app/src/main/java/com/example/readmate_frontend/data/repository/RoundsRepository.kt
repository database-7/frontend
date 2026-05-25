package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.RoundsApi
import com.example.readmate_frontend.data.model.request.CreatePostRequest
import com.example.readmate_frontend.data.model.response.posts.PostDetailData
import com.example.readmate_frontend.data.model.response.rounds.MyStatusData
import com.example.readmate_frontend.data.model.response.rounds.PostItem
import com.example.readmate_frontend.data.model.response.rounds.RoundItem
import javax.inject.Inject

class RoundsRepository @Inject constructor(
    private val roundsApi: RoundsApi
) {

    suspend fun getRounds(groupId: Int, categoryId: Int): List<RoundItem> =
        roundsApi.getRounds(groupId, categoryId).data

    suspend fun getPosts(groupId: Int, categoryId: Int, roundId: Int): List<PostItem> =
        roundsApi.getPosts(groupId, categoryId, roundId).data

    suspend fun createPost(groupId: Int, categoryId: Int, roundId: Int, request: CreatePostRequest) =
        roundsApi.createPost(groupId, categoryId, roundId, request)

    suspend fun getMyStatus(groupId: Int, categoryId: Int, roundId: Int): MyStatusData =
        roundsApi.getMyStatus(groupId, categoryId, roundId).data
}