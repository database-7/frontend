package com.example.readmate_frontend.data.model.response.bookcards

data class BookcardListResponse(
    val id: Int,
    val bookId: Int,
    val isbn: String,
    val title: String,
    val author: String,
    val coverImageUrl: String,
    val publisher: String,
    val review: String?,
    val rating: Int?,
    val startDate: String?,
    val endDate: String?,
    val status: BookStatus,
    val createdAt: String,
    val updatedAt: String
)

enum class BookStatus {
    WISH,
    READING,
    COMPLETED,
    PAUSED
}