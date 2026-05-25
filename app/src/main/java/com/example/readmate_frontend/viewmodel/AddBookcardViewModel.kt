package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.model.request.AddBookcardRequest
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.data.repository.BookcardsRepository
import com.example.readmate_frontend.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddBookCardUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

data class BookSearchUiState(
    val isLoading: Boolean = false,
    val results: List<BookSearchItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class AddBookCardViewModel @Inject constructor(
    private val bookcardsRepository: BookcardsRepository,
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBookCardUiState())
    val uiState: StateFlow<AddBookCardUiState> = _uiState

    private val _searchUiState = MutableStateFlow(BookSearchUiState())
    val searchUiState: StateFlow<BookSearchUiState> = _searchUiState

    private val _selectedBook = MutableStateFlow<BookSearchItem?>(null)
    val selectedBook: StateFlow<BookSearchItem?> = _selectedBook

    private val _selectedBookId = MutableStateFlow<Int?>(null)

    fun searchBooks(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchUiState.value = BookSearchUiState(isLoading = true)
            try {
                val results = booksRepository.searchBooks(query)
                _searchUiState.value = BookSearchUiState(results = results)
            } catch (e: Exception) {
                _searchUiState.value = BookSearchUiState(error = e.message)
            }
        }
    }

    fun selectBook(book: BookSearchItem) {
        viewModelScope.launch {
            try {
                val bookId = booksRepository.selectBook(book)
                _selectedBookId.value = bookId
                _selectedBook.value = book
            } catch (e: Exception) {
                _uiState.value = AddBookCardUiState(error = "책 선택 실패: ${e.message}")
            }
        }
    }

    fun clearSearch() {
        _searchUiState.value = BookSearchUiState()
    }

    fun addBookCard(
        review: String,
        rating: Int,
        startDate: String,
        endDate: String?,
        status: BookStatus
    ) {
        val bookId = _selectedBookId.value
        if (bookId == null) {
            _uiState.value = AddBookCardUiState(error = "책을 먼저 선택해주세요.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AddBookCardUiState(isLoading = true)
            try {
                bookcardsRepository.addBookCard(
                    AddBookcardRequest(
                        bookId = bookId,
                        review = review.ifEmpty { null },
                        rating = if (rating == 0) null else rating,
                        startDate = startDate,
                        endDate = endDate,
                        status = status
                    )
                )
                _uiState.value = AddBookCardUiState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = AddBookCardUiState(error = e.message)
            }
        }
    }
}