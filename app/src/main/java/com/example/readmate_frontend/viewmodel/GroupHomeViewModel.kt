package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.local.UserPreferences
import com.example.readmate_frontend.data.model.request.CreateCategoryRequest
import com.example.readmate_frontend.data.model.response.books.BookSearchItem
import com.example.readmate_frontend.data.model.response.groups.GroupHomeData
import com.example.readmate_frontend.data.model.response.groups.GroupMemberItem
import com.example.readmate_frontend.data.model.response.groups.MemberRole
import com.example.readmate_frontend.data.model.response.rounds.MyStatusData
import com.example.readmate_frontend.data.model.response.rounds.PostItem
import com.example.readmate_frontend.data.model.response.rounds.RoundItem
import com.example.readmate_frontend.data.repository.BooksRepository
import com.example.readmate_frontend.data.repository.CategoriesRepository
import com.example.readmate_frontend.data.repository.GroupsRepository
import com.example.readmate_frontend.data.repository.NotificationsRepository
import com.example.readmate_frontend.data.repository.RoundsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupHomeUiState(
    val isLoading: Boolean = false,
    val groupHome: GroupHomeData? = null,
    val error: String? = null
)

data class MembersUiState(
    val isLoading: Boolean = false,
    val members: List<GroupMemberItem> = emptyList(),
    val error: String? = null
)

data class BookSearchState(
    val query: String = "",
    val results: List<BookSearchItem> = emptyList(),
    val isSearching: Boolean = false,
    val selectedBook: BookSearchItem? = null,
    val selectedBookId: Int? = null
)

data class RoundWithPosts(
    val round: RoundItem,
    val posts: List<PostItem>
)

data class PostsUiState(
    val isLoading: Boolean = false,
    val roundsWithPosts: List<RoundWithPosts> = emptyList(),
    val currentRound: RoundItem? = null,
    val myStatus: MyStatusData? = null,
    val currentTurnUserName: String? = null,
    val currentTurnUserId: String? = null,
    val error: String? = null
)

@HiltViewModel
class GroupHomeViewModel @Inject constructor(
    private val repository: GroupsRepository,
    private val categoriesRepository: CategoriesRepository,
    private val booksRepository: BooksRepository,
    private val roundsRepository: RoundsRepository,
    private val userPreferences: UserPreferences,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private var currentGroupId = -1

    private val _uiState = MutableStateFlow(GroupHomeUiState())
    val uiState: StateFlow<GroupHomeUiState> = _uiState.asStateFlow()

    private val _membersState = MutableStateFlow(MembersUiState())
    val membersState: StateFlow<MembersUiState> = _membersState.asStateFlow()

    private val _bookSearch = MutableStateFlow(BookSearchState())
    val bookSearch: StateFlow<BookSearchState> = _bookSearch.asStateFlow()

    private val _postsState = MutableStateFlow(PostsUiState())
    val postsState: StateFlow<PostsUiState> = _postsState.asStateFlow()

    private val _isOwner = MutableStateFlow(false)
    val isOwner: StateFlow<Boolean> = _isOwner.asStateFlow()

    private var searchJob: Job? = null

    fun loadGroupHome(groupId: Int) {
        currentGroupId = groupId
        viewModelScope.launch {
            _uiState.value = GroupHomeUiState(isLoading = true)
            try {
                val groupHome = repository.getGroupHome(groupId)
                _uiState.value = GroupHomeUiState(groupHome = groupHome)
            } catch (e: Exception) {
                _uiState.value = GroupHomeUiState(error = e.message)
            }
            loadIsOwner(groupId)
        }
    }

    private suspend fun loadIsOwner(groupId: Int) {
        try {
            val myUserNum = userPreferences.getUserNum()
            val members = repository.getGroupMembers(groupId)
            val myRole = members
                .find { it.userNum == myUserNum }
                ?.role
                ?: MemberRole.MEMBER
            _isOwner.value = myRole == MemberRole.OWNER
        } catch (e: Exception) {
            _isOwner.value = false
        }
    }

    fun loadGroupMembers() {
        viewModelScope.launch {
            _membersState.value = MembersUiState(isLoading = true)
            try {
                val members = repository.getGroupMembers(currentGroupId)
                _membersState.value = MembersUiState(members = members)
            } catch (e: Exception) {
                _membersState.value = MembersUiState(error = e.message)
            }
        }
    }

    fun createCategory(request: CreateCategoryRequest) {
        viewModelScope.launch {
            try {
                categoriesRepository.createCategory(currentGroupId, request)
                loadGroupHome(currentGroupId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                categoriesRepository.deleteCategory(currentGroupId, categoryId)
                loadGroupHome(currentGroupId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun searchBooks(query: String) {
        _bookSearch.value = _bookSearch.value.copy(query = query, selectedBook = null, selectedBookId = null)
        if (query.isBlank()) {
            _bookSearch.value = _bookSearch.value.copy(results = emptyList(), isSearching = false)
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            _bookSearch.value = _bookSearch.value.copy(isSearching = true)
            try {
                val results = booksRepository.searchBooks(query)
                _bookSearch.value = _bookSearch.value.copy(results = results, isSearching = false)
            } catch (e: Exception) {
                _bookSearch.value = _bookSearch.value.copy(isSearching = false)
            }
        }
    }

    fun selectBook(book: BookSearchItem) {
        viewModelScope.launch {
            try {
                val bookId = booksRepository.selectBook(book)
                _bookSearch.value = _bookSearch.value.copy(
                    selectedBook = book,
                    selectedBookId = bookId,
                    results = emptyList()
                )
            } catch (e: Exception) { }
        }
    }

    fun resetBookSearch() {
        searchJob?.cancel()
        _bookSearch.value = BookSearchState()
    }

    fun loadPosts(categoryId: Int) {
        viewModelScope.launch {
            _postsState.value = PostsUiState(isLoading = true)
            try {
                val rounds = roundsRepository.getRounds(currentGroupId, categoryId)
                val ongoingRound = rounds.find { it.status == "ONGOING" }

                if (_membersState.value.members.isEmpty()) {
                    try {
                        val members = repository.getGroupMembers(currentGroupId)
                        _membersState.value = MembersUiState(members = members)
                    } catch (e: Exception) { }
                }
                val members = _membersState.value.members

                val roundsWithPosts = rounds.map { round ->
                    async {
                        val posts = try {
                            roundsRepository.getPosts(currentGroupId, categoryId, round.roundId)
                        } catch (e: Exception) { emptyList() }
                        RoundWithPosts(round = round, posts = posts)
                    }
                }.awaitAll()

                val myStatus = if (ongoingRound != null) {
                    try { roundsRepository.getMyStatus(currentGroupId, categoryId, ongoingRound.roundId) }
                    catch (e: Exception) { null }
                } else null

                val currentTurnUserName = computeCurrentTurnUserName(
                    roundsWithPosts, ongoingRound, members, userPreferences.getUserNum()
                )

                val currentTurnUserId = computeCurrentTurnUserId(
                    roundsWithPosts, ongoingRound, members, userPreferences.getUserNum()
                )

                _postsState.value = PostsUiState(
                    roundsWithPosts = roundsWithPosts,
                    currentRound = ongoingRound ?: rounds.lastOrNull(),
                    myStatus = myStatus,
                    currentTurnUserName = currentTurnUserName,
                    currentTurnUserId = currentTurnUserId
                )
            } catch (e: Exception) {
                _postsState.value = PostsUiState(error = e.message)
            }
        }
    }

    private fun computeCurrentTurnUserId(
        roundsWithPosts: List<RoundWithPosts>,
        ongoingRound: RoundItem?,
        members: List<GroupMemberItem>,
        myUserNum: Int
    ): String? {
        if (ongoingRound == null || members.isEmpty()) return null
        val ongoingPosts = roundsWithPosts
            .find { it.round.roundId == ongoingRound.roundId }?.posts ?: emptyList()
        val readingOrder = roundsWithPosts
            .filter { it.round.status == "COMPLETED" && it.posts.size == members.size }
            .maxByOrNull { it.round.roundNumber }
            ?.posts?.map { it.userId }
        return if (readingOrder != null) {
            val nextIndex = ongoingPosts.size
            if (nextIndex < readingOrder.size) {
                val nextUserNum = readingOrder[nextIndex]
                members.find { it.userNum == nextUserNum }?.userId
            } else null
        } else null
    }

    fun sendPressure(userId: String) {
        viewModelScope.launch {
            try { notificationsRepository.sendPressure(userId) } catch (e: Exception) { }
        }
    }

    private fun computeCurrentTurnUserName(
        roundsWithPosts: List<RoundWithPosts>,
        ongoingRound: RoundItem?,
        members: List<GroupMemberItem>,
        myUserNum: Int
    ): String? {
        if (ongoingRound == null || members.isEmpty()) return null

        val ongoingPosts = roundsWithPosts
            .find { it.round.roundId == ongoingRound.roundId }?.posts ?: emptyList()

        val readingOrder = roundsWithPosts
            .filter { it.round.status == "COMPLETED" && it.posts.size == members.size }
            .maxByOrNull { it.round.roundNumber }
            ?.posts?.map { it.userId }

        return if (readingOrder != null) {
            val nextIndex = ongoingPosts.size
            if (nextIndex < readingOrder.size) {
                val nextUserId = readingOrder[nextIndex]
                val name = members.find { it.userNum == nextUserId }?.userName
                if (nextUserId == myUserNum) "나" else name
            } else null
        } else {
            if (ongoingPosts.isEmpty()) {
                null
            } else null
        }
    }
}