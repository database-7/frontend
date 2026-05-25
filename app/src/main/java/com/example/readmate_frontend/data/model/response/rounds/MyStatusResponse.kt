package com.example.readmate_frontend.data.model.response.rounds

data class MyStatusResponse(
    val success: Boolean,
    val message: String?,
    val data: MyStatusData
)

data class MyStatusData(
    val roundId: Int,
    val roundNumber: Int,
    val startPage: Int,
    val endPage: Int,
    val myOrderIndex: Int,
    val turnStartedAt: String,
    val deadlineAt: String,
    val submitted: Boolean,
    val blacklisted: Boolean,
    val myTurn: Boolean
)
