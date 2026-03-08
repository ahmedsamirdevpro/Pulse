package com.ahmedsamir.pulse.feature.comments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedsamir.pulse.core.model.Comment
import com.ahmedsamir.pulse.core.model.Resource
import com.ahmedsamir.pulse.feature.comments.domain.usecase.AddCommentUseCase
import com.ahmedsamir.pulse.feature.comments.domain.usecase.GetCommentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCommentsUseCase(postId).collect { comments ->
                _uiState.update { it.copy(isLoading = false, comments = comments) }
            }
        }
    }

    fun onCommentChange(content: String) {
        _uiState.update { it.copy(commentInput = content, error = null) }
    }

    fun addComment(postId: String) {
        viewModelScope.launch {
            val content = _uiState.value.commentInput
            _uiState.update { it.copy(isSending = true, error = null) }
            when (val result = addCommentUseCase(postId, content)) {
                is Resource.Success -> _uiState.update {
                    it.copy(isSending = false, commentInput = "")
                }
                is Resource.Error -> _uiState.update {
                    it.copy(isSending = false, error = result.message)
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

data class CommentsUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val commentInput: String = "",
    val error: String? = null
)