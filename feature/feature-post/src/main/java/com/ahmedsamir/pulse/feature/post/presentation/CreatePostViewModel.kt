package com.ahmedsamir.pulse.feature.post.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.post.domain.usecase.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun onContentChange(content: String) {
        _uiState.update {
            it.copy(
                content = content,
                remainingChars = 280 - content.length,
                error = null
            )
        }
    }

    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun createPost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = createPostUseCase(
                _uiState.value.content,
                _uiState.value.imageUri
            )) {
                is Resource.Success -> _uiState.update {
                    it.copy(isLoading = false, isSuccess = true)
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isLoading = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

data class CreatePostUiState(
    val content: String = "",
    val imageUri: String? = null,
    val remainingChars: Int = 280,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)