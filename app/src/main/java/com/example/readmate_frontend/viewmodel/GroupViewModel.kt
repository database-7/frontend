package com.example.readmate_frontend.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readmate_frontend.data.local.UserPreferences
import com.example.readmate_frontend.data.model.response.groups.GroupItem
import com.example.readmate_frontend.data.model.response.groups.MemberRole
import com.example.readmate_frontend.data.repository.GroupsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupUiState(
    val isLoading: Boolean = false,
    val groups: List<GroupItem> = emptyList(),
    val inviteCodes: Map<Int, String> = emptyMap(),
    val userRoles: Map<Int, MemberRole> = emptyMap(),
    val error: String? = null
)

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: GroupsRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupUiState())
    val uiState: StateFlow<GroupUiState> = _uiState

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = GroupUiState(isLoading = true)
            try {
                val groups = repository.getGroups()
                _uiState.value = GroupUiState(groups = groups)
                val groupIds = groups.map { it.groupId }
                loadInviteCodes(groupIds)
                loadUserRoles(groupIds)
            } catch (e: Exception) {
                _uiState.value = GroupUiState(error = e.message)
            }
        }
    }

    private fun loadUserRoles(groupIds: List<Int>) {
        viewModelScope.launch {
            val myUserNum = userPreferences.getUserNum()
            val roles = mutableMapOf<Int, MemberRole>()
            groupIds.forEach { groupId ->
                try {
                    val members = repository.getGroupMembers(groupId)
                    val myRole = members
                        .find { it.userNum == myUserNum }
                        ?.role
                        ?: MemberRole.MEMBER
                    roles[groupId] = myRole
                } catch (e: Exception) {
                    roles[groupId] = MemberRole.MEMBER
                }
            }
            _uiState.value = _uiState.value.copy(userRoles = roles)
        }
    }

    private fun loadInviteCodes(groupIds: List<Int>) {
        viewModelScope.launch {
            val codes = mutableMapOf<Int, String>()
            groupIds.forEach { groupId ->
                try {
                    val result = repository.getInviteCode(groupId)
                    result.values.firstOrNull()?.let { codes[groupId] = it }
                } catch (e: Exception) { }
            }
            _uiState.value = _uiState.value.copy(inviteCodes = codes)
        }
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            try {
                repository.createGroup(name)
                loadGroups()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun joinGroup(inviteCode: String) {
        viewModelScope.launch {
            try {
                repository.joinGroup(inviteCode)
                loadGroups()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateGroup(groupId: Int, newName: String) {
        viewModelScope.launch {
            try {
                repository.updateGroup(groupId, newName)
                loadGroups()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteGroup(groupId)
                loadGroups()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun leaveGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                repository.leaveGroup(groupId)
                loadGroups()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}