package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.PostsApi
import com.example.readmate_frontend.data.model.response.posts.PostDetailData
import javax.inject.Inject

class PostsRepository @Inject constructor(
    private val postsApi: PostsApi
){
    suspend fun getPostDetail(groupId: Int, postId: Int): PostDetailData =
        postsApi.getPostDetail(groupId, postId).data
}