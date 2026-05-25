package com.example.readmate_frontend.data.model.request

import com.example.readmate_frontend.data.model.response.bookcards.BookStatus

data class UpdateBookcardRequest(
    val review: String?,
    val rating: Int?,
    val startDate: String?,
    val endDate: String?,
    val status: BookStatus
)