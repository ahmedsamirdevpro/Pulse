package com.ahmedsamir.pulse.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.core.model.User
import com.ahmedsamir.pulse.feature.profile.domain.usecase.FollowUserUseCase
import com.ahmedsamir.pulse.feature.profile.domain.usecase.GetProfileUseCase
import com.ahmedsamir.pulse.feature.profile.domain.usecase.UpdateProfileUseCase
import com.ahmedsamir.pulse.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    val currentUserId: String? get() = profileRepository.getCurrentUserId()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getProfileUseCase(userId).collect { user ->
                val isFollowing = user?.let {
                    profileRepository.isFollowing(it.id)
                } ?: false
                _uiState.update {
                    it.copy(isLoading = false, user = user, isFollowing = isFollowing)
                }
            }
        }
    }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            val isFollowing = _uiState.value.isFollowing
            followUserUseCase(userId, isFollowing)
            _uiState.update { it.copy(isFollowing = !isFollowing) }
        }
    }

    fun updateProfile(displayName: String, bio: String, imageUri: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true, error = null) }
            when (val result = updateProfileUseCase(displayName, bio, imageUri)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isUpdating = false, user = result.data, isEditSuccess = true)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isUpdating = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun clearEditSuccess() {
        _uiState.update { it.copy(isEditSuccess = false) }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val user: User? = null,
    val isFollowing: Boolean = false,
    val isEditSuccess: Boolean = false,
    val error: String? = null
)