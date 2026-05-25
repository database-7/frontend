package com.example.readmate_frontend.data.repository

import com.example.readmate_frontend.data.api.BookcardsApi
import com.example.readmate_frontend.data.model.request.AddBookcardRequest
import com.example.readmate_frontend.data.model.request.UpdateBookcardRequest
import com.example.readmate_frontend.data.model.response.bookcards.AddBookcardResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.data.model.response.bookcards.BookcardDetailResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookcardListResponse
import javax.inject.Inject

class BookcardsRepository @Inject constructor(
    private val bookcardsApi: BookcardsApi
) {
    suspend fun getBookCardDetail(cardId: Int): BookcardDetailResponse {
        return bookcardsApi.getBookCardDetail(cardId)
    }

    suspend fun getBookCardList(status: BookStatus? = null): List<BookcardListResponse> {
        return bookcardsApi.getBookCardList(status)
    }

    suspend fun addBookCard(request: AddBookcardRequest): AddBookcardResponse {
        return bookcardsApi.addBookCard(request)
    }

    suspend fun updateBookCard(cardId: Int, request: UpdateBookcardRequest): BookcardListResponse {
        return bookcardsApi.updateBookCard(cardId, request)
    }

    suspend fun deleteBookCard(cardId: Int) {
        bookcardsApi.deleteBookCard(cardId)
    }
}
