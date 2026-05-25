package com.example.readmate_frontend.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.model.request.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val api: UsersApi
) : ViewModel() {

    private val _userId   = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _userName = MutableStateFlow("")

    val userId:   StateFlow<String> = _userId.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUserIdChange(value: String)   { _userId.value   = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onUserNameChange(value: String) { _userName.value = value }

    fun register() {
        android.util.Log.d("RegisterVM", "register() 호출됨 - userId: ${_userId.value}, password: ${_password.value}, userName: ${_userName.value}")

        if (_userId.value.isBlank() || _password.value.isBlank() || _userName.value.isBlank()) {
            _uiState.value = RegisterUiState.Error("모든 항목을 입력해주세요.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            try {
                val response = api.register(
                    RegisterRequest(
                        userId   = _userId.value.trim(),
                        password = _password.value,
                        userName = _userName.value.trim()
                    )
                )
                if (response.success) {
                    _uiState.value = RegisterUiState.Success
                } else {
                    _uiState.value = RegisterUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}