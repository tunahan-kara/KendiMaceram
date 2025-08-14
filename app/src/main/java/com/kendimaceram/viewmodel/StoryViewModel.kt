package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.StoryNode
import com.kendimaceram.app.data.StoryRepository
import com.kendimaceram.app.data.StoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    private var storyNodes: Map<String, StoryNode> = emptyMap()

    // init bloğu artık boş. Hikaye, dışarıdan gelen komutla yüklenecek.
    init {}

    // StoryReaderScreen'in aradığı ve hata veren fonksiyon buydu. Artık var!
    fun loadStory(storyDocId: String) {
        // Eğer geçersiz bir ID gelirse (örneğin ilk açılışta), bir şey yapma.
        if (storyDocId == "N/A" || storyDocId.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            storyNodes = StoryRepository.fetchStoryNodes(storyDocId)
            val startNode = storyNodes["start"]
            _uiState.update { it.copy(currentNode = startNode, isLoading = false) }
        }
    }

    fun makeChoice(choiceNodeId: String) {
        val nextNode = storyNodes[choiceNodeId]
        _uiState.update { it.copy(currentNode = nextNode, highlightRange = null) }
    }

    fun updateHighlightRange(range: IntRange) {
        _uiState.update { it.copy(highlightRange = range) }
    }

    fun clearHighlight() {
        _uiState.update { it.copy(highlightRange = null) }
    }
}