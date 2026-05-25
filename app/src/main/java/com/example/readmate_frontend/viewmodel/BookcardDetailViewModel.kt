package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.model.response.bookcards.BookcardDetailResponse
import com.example.readmate_frontend.data.model.response.bookcards.BookStatus
import com.example.readmate_frontend.data.repository.BookcardsRepository
import com.example.readmate_frontend.ui.bookcard.BookCardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookCardUiState(
    val isLoading: Boolean = false,
    val cards: List<BookCardData> = emptyList(),
    val initialPage: Int = 0,
    val error: String? = null
)

fun BookcardDetailResponse.toBookCardData(): BookCardData {
    val days = try {
        if (startDate != null && endDate != null) {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)
            if (start != null && end != null) {
                val diff = (end.time - start.time) / (1000 * 60 * 60 * 24) + 1
                "${diff}일"
            } else ""
        } else ""
    } catch (e: Exception) { "" }

    return BookCardData(
        title = title,
        author = author,
        publisher = publisher,
        year = createdAt.take(4),
        rating = rating ?: 0,
        isCompleted = status == BookStatus.COMPLETED,
        startDate = startDate ?: "",
        endDate = endDate ?: "",
        days = days,
        noteDate = updatedAt.take(10),
        note = review ?: "",
        coverImageUrl = coverImageUrl
    )
}

@HiltViewModel
class BookCardViewModel @Inject constructor(
    private val repository: BookcardsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookCardUiState())
    val uiState: StateFlow<BookCardUiState> = _uiState

    fun loadAllCards(startCardId: Int) {
        viewModelScope.launch {
            _uiState.value = BookCardUiState(isLoading = true)
            try {
                val list = repository.getBookCardList()
                val cards = list.map { repository.getBookCardDetail(it.id).toBookCardData() }
                val initialPage = list.indexOfFirst { it.id == startCardId }.coerceAtLeast(0)
                _uiState.value = BookCardUiState(
                    isLoading = false,
                    cards = cards,
                    initialPage = initialPage
                )
            } catch (e: Exception) {
                _uiState.value = BookCardUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun loadCards(cardIds: List<Int>) {
        viewModelScope.launch {
            _uiState.value = BookCardUiState(isLoading = true)
            try {
                val cards = cardIds.map { repository.getBookCardDetail(it).toBookCardData() }
                _uiState.value = BookCardUiState(isLoading = false, cards = cards)
            } catch (e: Exception) {
                _uiState.value = BookCardUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun loadCard(cardId: Int) {
        viewModelScope.launch {
            _uiState.value = BookCardUiState(isLoading = true)
            try {
                val card = repository.getBookCardDetail(cardId).toBookCardData()
                _uiState.value = BookCardUiState(isLoading = false, cards = listOf(card))
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 404) {
                    _uiState.value = BookCardUiState(isLoading = false, cards = emptyList())
                } else {
                    _uiState.value = BookCardUiState(isLoading = false, error = e.message)
                }
            } catch (e: Exception) {
                _uiState.value = BookCardUiState(isLoading = false, error = e.message)
            }
        }
    }
}

