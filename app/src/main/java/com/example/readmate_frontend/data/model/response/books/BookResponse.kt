package com.example.readmate_frontend.data.model.response.books

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T
)

data class BookSearchItem(
    val isbn13: String,
    val title: String,
    val author: String,
    val publisher: String,
    val pubDate: String,
    val description: String,
    val coverImageUrl: String
)

data class BookDetailInfo(
    val isbn13: String,
    val title: String,
    val author: String,
    val publisher: String,
    val pubDate: String,
    val description: String,
    val coverImageUrl: String,
    val customerReviewRank: Int,
    val salesPoint: Int
)

data class GenreItem(
    val code: String,
    val name: String,
    val categoryId: Int
)