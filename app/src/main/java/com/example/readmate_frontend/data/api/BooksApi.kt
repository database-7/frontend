package com.example.readmate_frontend.data.api

import com.example.readmate_frontend.data.model.request.SelectBookRequest
import com.example.readmate_frontend.data.model.response.books.ApiResponse
import com.example.readmate_frontend.data.model.response.books.BookDetailInfo
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.books.GenreItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApi {
    @GET("api/books/search")
    suspend fun searchBooks(
        @Query("query") query: String
    ): ApiResponse<List<BookSearchItem>>

    @GET("api/books/{isbn13}")
    suspend fun getBookDetail(
        @Path("isbn13") isbn13: String
    ): ApiResponse<BookDetailInfo>

    @POST("api/books/select")
    suspend fun selectBook(
        @Body request: SelectBookRequest
    ): ApiResponse<Int>

    @GET("api/books/genres")
    suspend fun getGenres(): ApiResponse<List<GenreItem>>
}