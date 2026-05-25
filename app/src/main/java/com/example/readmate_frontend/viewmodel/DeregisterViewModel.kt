package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.UsersApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DeregisterUiState {
    object Idle : DeregisterUiState()
    object Loading : DeregisterUiState()
    object Success : DeregisterUiState()
    data class Error(val message: String) : DeregisterUiState()
}

@HiltViewModel
class DeregisterViewModel @Inject constructor(
    private val api: UsersApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeregisterUiState>(DeregisterUiState.Idle)
    val uiState: StateFlow<DeregisterUiState> = _uiState.asStateFlow()

    fun deregister() {
        viewModelScope.launch {
            _uiState.value = DeregisterUiState.Loading
            try {
                val response = api.deregister()
                if (response.success) {
                    _uiState.value = DeregisterUiState.Success
                } else {
                    _uiState.value = DeregisterUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = DeregisterUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
}