package com.example.readmate_frontend.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.HomeApi
import com.example.readmate_frontend.data.model.response.home.ArticleApiItem
import com.example.readmate_frontend.data.model.response.home.BookApiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val bestseller: List<BookApiItem> = emptyList(),
    val newBooks: List<BookApiItem> = emptyList(),
    val articles: List<ArticleApiItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeApi: HomeApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            try {
                val response = homeApi.getHomeData()
                if (response.success) {
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        userName = response.data.userName,
                        bestseller = response.data.bestseller,
                        newBooks = response.data.newBooks,
                        articles = response.data.articles
                    )
                } else {
                    _uiState.value = HomeUiState(isLoading = false, error = response.message)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState(isLoading = false, error = e.message)
            }
        }
    }
}