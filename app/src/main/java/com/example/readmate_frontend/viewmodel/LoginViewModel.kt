package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.api.UsersApi
import com.example.readmate_frontend.data.local.TokenStore
import com.example.readmate_frontend.data.local.UserPreferences
import com.example.readmate_frontend.data.model.request.LoginRequest
import com.example.readmate_frontend.data.repository.NotificationsRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val token: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: UsersApi,
    private val userPreferences: UserPreferences,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _userId   = MutableStateFlow("")
    private val _password = MutableStateFlow("")

    val userId:   StateFlow<String> = _userId.asStateFlow()
    val password: StateFlow<String> = _password.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUserIdChange(value: String)   { _userId.value   = value }
    fun onPasswordChange(value: String) { _password.value = value }

    fun login() {
        if (_userId.value.isBlank() || _password.value.isBlank()) {
            _uiState.value = LoginUiState.Error("아이디와 비밀번호를 입력해주세요.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = api.login(
                    LoginRequest(userId = _userId.value.trim(), password = _password.value)
                )
                if (response.success) {
                    TokenStore.token = response.data
                    val profile = api.profile()
                    profile.data?.let {
                        userPreferences.saveUserNum(it.userNum)
                        userPreferences.saveUserName(it.userName)
                        userPreferences.saveUserId(it.userId)
                    }
                    FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
                        viewModelScope.launch {
                            try { notificationsRepository.saveToken(fcmToken) } catch (e: Exception) { }
                        }
                    }
                    _uiState.value = LoginUiState.Success(response.data)
                } else {
                    _uiState.value = LoginUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}