package com.example.readmatefrontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.model.request.UpdateBookcardRequest
import com.example.readmate_frontend.data.repository.BookcardsRepository
import com.example.readmate_frontend.data.model.response.bookcards.BookcardListResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookCardListUiState(
    val isLoading: Boolean = false,
    val cards: List<BookcardListResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class BookcardListViewModel @Inject constructor(
    private val repository: BookcardsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookCardListUiState())
    val uiState: StateFlow<BookCardListUiState> = _uiState
    private var currentStatus: BookStatus? = null

    fun loadCards(status: BookStatus? = null) {
        currentStatus = status
        viewModelScope.launch {
            _uiState.value = BookCardListUiState(isLoading = true)
            try {
                val cards = repository.getBookCardList(status)
                _uiState.value = BookCardListUiState(isLoading = false, cards = cards)
            } catch (e: Exception) {
                _uiState.value = BookCardListUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteBookCard(cardId)
                _uiState.value = _uiState.value.copy(
                    cards = _uiState.value.cards.filter { it.id != cardId }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCard(cardId: Int, request: UpdateBookcardRequest) {
        viewModelScope.launch {
            try {
                repository.updateBookCard(cardId, request)
                loadCards(currentStatus)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }


}