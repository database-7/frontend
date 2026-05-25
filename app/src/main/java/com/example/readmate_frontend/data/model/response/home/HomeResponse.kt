package com.example.readmate_frontend.data.model.response.home

data class HomeResponse(
    val success: Boolean,
    val message: String,
    val data: HomeData
)

data class HomeData(
    val userName: String,
    val bestseller: List<BookApiItem>,
    val newBooks: List<BookApiItem>,
    val articles: List<ArticleApiItem>
)

data class BookApiItem(
    val isbn13: String,
    val title: String,
    val author: String,
    val coverImageUrl: String,
    val pubDate: String
)

data class ArticleApiItem(
    val title: String,
    val link: String,
    val description: String,
    val pubDate: String
)