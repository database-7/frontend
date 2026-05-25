package com.example.readmate_frontend.data.model.response.rounds

data class GetRoundsResponse(
    val success: Boolean,
    val message: String?,
    val data: List<RoundItem>
)

data class RoundItem(
    val roundId: Int,
    val roundNumber: Int,
    val startPage: Int,
    val endPage: Int,
    val status: String
)
