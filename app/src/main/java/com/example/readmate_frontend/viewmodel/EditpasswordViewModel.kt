package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.model.request.EditprofileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditPasswordUiState {
    object Idle : EditPasswordUiState()
    object Loading : EditPasswordUiState()
    object Success : EditPasswordUiState()
    data class Error(val message: String) : EditPasswordUiState()
}

@HiltViewModel
class EditPasswordViewModel @Inject constructor(
    private val api: UsersApi
) : ViewModel() {

    private val _newPassword = MutableStateFlow("")
    private val _confirmPassword = MutableStateFlow("")

    val newPassword: StateFlow<String> = _newPassword.asStateFlow()
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _uiState = MutableStateFlow<EditPasswordUiState>(EditPasswordUiState.Idle)
    val uiState: StateFlow<EditPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String) { _newPassword.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }

    fun editPassword() {
        if (_newPassword.value.isBlank() || _confirmPassword.value.isBlank()) {
            _uiState.value = EditPasswordUiState.Error("비밀번호를 입력해주세요.")
            return
        }
        if (_newPassword.value != _confirmPassword.value) {
            _uiState.value = EditPasswordUiState.Error("비밀번호가 일치하지 않습니다.")
            return
        }

        viewModelScope.launch {
            _uiState.value = EditPasswordUiState.Loading
            try {
                val profile = api.profile()
                if (!profile.success || profile.data == null) {
                    _uiState.value = EditPasswordUiState.Error("프로필 조회 실패")
                    return@launch
                }

                val response = api.editprofile(
                    EditprofileRequest(
                        userId = profile.data.userId,
                        password = _newPassword.value,
                        userName = profile.data.userName
                    )
                )
                if (response.success) {
                    _uiState.value = EditPasswordUiState.Success
                } else {
                    _uiState.value = EditPasswordUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = EditPasswordUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    fun resetState() { _uiState.value = EditPasswordUiState.Idle }
}