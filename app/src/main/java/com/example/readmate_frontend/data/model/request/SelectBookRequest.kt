package com.example.readmate_frontend.data.model.request

data class SelectBookRequest(
    val isbn13: String,
    val title: String,
    val author: String,
    val publisher: String,
    val pubDate: String,
    val description: String,
    val coverImageUrl: String
)
