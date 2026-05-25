package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.model.request.CreatePostRequest
import com.example.readmate_frontend.data.model.response.rounds.RoundItem
import com.example.readmate_frontend.data.repository.RoundsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreatePostState {
    object Idle : CreatePostState()
    object Loading : CreatePostState()
    object Success : CreatePostState()
    data class Error(val message: String) : CreatePostState()
}

@HiltViewModel
class PostViewModel @Inject constructor(
    private val roundsRepository: RoundsRepository
) : ViewModel() {

    private val _currentRound = MutableStateFlow<RoundItem?>(null)
    val currentRound: StateFlow<RoundItem?> = _currentRound.asStateFlow()

    private val _createState = MutableStateFlow<CreatePostState>(CreatePostState.Idle)
    val createState: StateFlow<CreatePostState> = _createState.asStateFlow()

    fun loadCurrentRound(groupId: Int, categoryId: Int) {
        viewModelScope.launch {
            try {
                val rounds = roundsRepository.getRounds(groupId, categoryId)
                _currentRound.value = rounds.find { it.status == "ONGOING" } ?: rounds.lastOrNull()
            } catch (e: Exception) {
                _currentRound.value = null
            }
        }
    }

    fun createPost(groupId: Int, categoryId: Int, title: String, content: String) {
        val roundId = _currentRound.value?.roundId
        if (roundId == null) {
            _createState.value = CreatePostState.Error("진행 중인 라운드가 없습니다.")
            return
        }
        viewModelScope.launch {
            _createState.value = CreatePostState.Loading
            try {
                roundsRepository.createPost(
                    groupId, categoryId, roundId,
                    CreatePostRequest(title = title, content = content)
                )
                _createState.value = CreatePostState.Success
            } catch (e: Exception) {
                _createState.value = CreatePostState.Error(e.message ?: "오류가 발생했습니다.")
            }
        }
    }

    /*fun createPost(groupId: Int, categoryId: Int, title: String, content: String) {
        val roundId = _currentRound.value?.roundId ?: return
        viewModelScope.launch {
            _createState.value = CreatePostState.Loading
            try {
                roundsRepository.createPost(
                    groupId, categoryId, roundId,
                    CreatePostRequest(title = title, content = content)
                )
                _createState.value = CreatePostState.Success
            } catch (e: Exception) {
                _createState.value = CreatePostState.Error(e.message ?: "오류가 발생했습니다.")
            }
        }
    }*/

    fun resetState() {
        _createState.value = CreatePostState.Idle
    }
}