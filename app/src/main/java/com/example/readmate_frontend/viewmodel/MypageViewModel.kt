package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.model.request.EditprofileRequest
import com.example.readmate_frontend.data.model.response.users.ProfileData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val data: ProfileData) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: UsersApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchProfile()
    }

    fun changeNickname(newName: String) {
        val currentState = _uiState.value as? ProfileUiState.Success ?: return
        val userId = currentState.data.userId

        viewModelScope.launch {
            try {
                api.editprofile(
                    EditprofileRequest(
                        userId = userId,
                        password = "",       // 서버에서 userName만 바꾸면 무시됨
                        userName = newName
                    )
                )
                fetchProfile()
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("변경 실패: ${e.message}")
            }
        }
    }
    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                val response = api.profile()
                if (response.success && response.data != null) {
                    _uiState.value = ProfileUiState.Success(response.data)
                } else {
                    _uiState.value = ProfileUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
}