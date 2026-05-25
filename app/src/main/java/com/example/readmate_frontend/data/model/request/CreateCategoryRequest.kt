package com.example.readmate_frontend.data.model.request

data class CreateCategoryRequest(
    val bookId: Int,
    val readingMode: String,
    val pagesPerRound: Int,
    val daysPerRound: Int,
    val startAt: String?,
    val memberOrder: List<Int>,
    val totalPageCount: Int
)

