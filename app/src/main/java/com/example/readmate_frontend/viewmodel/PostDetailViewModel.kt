package com.example.readmate_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.local.UserPreferences
import com.example.readmate_frontend.data.model.response.posts.CommentItem
import com.example.readmate_frontend.data.model.response.posts.PostDetailData
import com.example.readmate_frontend.data.repository.CommentsRepository
import com.example.readmate_frontend.data.repository.PostsRepository
import com.example.readmate_frontend.data.repository.RoundsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PostDetailState {
    object Loading : PostDetailState()
    data class Success(val data: PostDetailData) : PostDetailState()
    data class Error(val message: String) : PostDetailState()
}

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val roundsRepository: RoundsRepository,
    private val userPreferences: UserPreferences,
    private val commentsRepository: CommentsRepository,
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PostDetailState>(PostDetailState.Loading)
    val state: StateFlow<PostDetailState> = _state.asStateFlow()

    private val _comments = MutableStateFlow<List<CommentItem>>(emptyList())
    val comments: StateFlow<List<CommentItem>> = _comments.asStateFlow()

    private val _commentLoading = MutableStateFlow(false)
    val commentLoading: StateFlow<Boolean> = _commentLoading.asStateFlow()

    val currentUserName: String get() = userPreferences.getUserName()

    private var currentGroupId = -1
    private var currentPostId = -1

    fun loadPostDetail(groupId: Int, postId: Int) {
        currentGroupId = groupId
        currentPostId = postId
        viewModelScope.launch {
            _state.value = PostDetailState.Loading
            try {
                val detail = postsRepository.getPostDetail(groupId, postId)
                _state.value = PostDetailState.Success(detail)
                loadComments()
            } catch (e: Exception) {
                _state.value = PostDetailState.Error(e.message ?: "오류가 발생했습니다.")
            }
        }
    }

    fun refreshComments() {
        viewModelScope.launch { loadComments() }
    }

    private suspend fun loadComments() {
        try {
            _comments.value = commentsRepository.getComments(currentGroupId, currentPostId)
        } catch (e: Exception) {
        }
    }

    fun createComment(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _commentLoading.value = true
            try {
                commentsRepository.createComment(currentGroupId, currentPostId, content)
                loadComments()
            } catch (e: Exception) {
            } finally {
                _commentLoading.value = false
            }
        }
    }

    fun updateComment(commentId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            _commentLoading.value = true
            try {
                commentsRepository.updateComment(currentGroupId, currentPostId, commentId, content)
                loadComments()
            } catch (e: Exception) {
            } finally {
                _commentLoading.value = false
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            _commentLoading.value = true
            try {
                commentsRepository.deleteComment(currentGroupId, currentPostId, commentId)
                loadComments()
            } catch (e: Exception) {
            } finally {
                _commentLoading.value = false
            }
        }
    }
}