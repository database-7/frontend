package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.BooksApi
import com.example.readmate_frontend.data.model.request.SelectBookRequest
import com.example.readmate_frontend.data.model.response.books.BookDetailInfo
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.books.GenreItem
import javax.inject.Inject

class BooksRepository @Inject constructor(
    private val booksApi: BooksApi) {

    suspend fun searchBooks(query: String): List<BookSearchItem> {
        return booksApi.searchBooks(query).data
    }

    suspend fun getBookDetail(isbn13: String): BookDetailInfo {
        return booksApi.getBookDetail(isbn13).data
    }

    suspend fun selectBook(book: BookSearchItem): Int {
        return booksApi.selectBook(
            SelectBookRequest(
                isbn13 = book.isbn13,
                title = book.title,
                author = book.author,
                publisher = book.publisher,
                pubDate = book.pubDate,
                description = book.description,
                coverImageUrl = book.coverImageUrl
            )
        ).data
    }

    suspend fun getGenres(): List<GenreItem> {
        return booksApi.getGenres().data
    }

}