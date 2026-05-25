package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.AddBookcardRequest
import com.example.readmate_frontend.data.model.request.UpdateBookcardRequest
import com.example.readmate_frontend.data.model.response.bookcards.AddBookcardResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.data.model.response.bookcards.BookcardDetailResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookcardListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BookcardsApi {
    @GET("api/bookcards/{cardId}")
    suspend fun getBookCardDetail(
        @Path("cardId") cardId: Int): BookcardDetailResponse

    @PUT("api/bookcards/{cardId}")
    suspend fun updateBookCard(
        @Path("cardId") cardId: Int, @Body request: UpdateBookcardRequest): BookcardListResponse

    @DELETE("api/bookcards/{cardId}")
    suspend fun deleteBookCard(@Path("cardId") cardId: Int): retrofit2.Response<Unit>

    @GET("api/bookcards")
    suspend fun getBookCardList(
        @Query("status") status: BookStatus? = null
    ): List<BookcardListResponse>

    @POST("api/bookcards")
    suspend fun addBookCard(
        @Body request: AddBookcardRequest
    ): AddBookcardResponse



}