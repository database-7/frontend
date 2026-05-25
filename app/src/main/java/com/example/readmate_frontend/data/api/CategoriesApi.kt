package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.CreateCategoryRequest
import com.example.readmate_frontend.data.model.response.category.CreateCategoryResponse
import com.example.readmate_frontend.data.model.response.category.GetCategoriesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoriesApi {

    @GET("api/groups/{groupId}/categories")
    suspend fun getCategories(
        @Path("groupId") groupId: Int
    ): GetCategoriesResponse

    @POST("api/groups/{groupId}/categories")
    suspend fun createCategory(
        @Path("groupId") groupId: Int,
        @Body request: CreateCategoryRequest
    ): CreateCategoryResponse

    @DELETE("api/groups/{groupId}/categories/{categoryId}")
    suspend fun deleteCategory(
        @Path("groupId") groupId: Int,
        @Path("categoryId") categoryId: Int
    ): Response<Unit>
}