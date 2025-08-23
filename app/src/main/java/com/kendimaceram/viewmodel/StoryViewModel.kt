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
class StoryViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    private var storyNodes: Map<String, StoryNode> = emptyMap()

    // Bu fonksiyon artık çok daha akıllı.
    fun loadStory(storyDocId: String) {
        if (storyDocId.isEmpty() || storyDocId == "{storyId}") return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Önce lokalde (telefonda) hikaye var mı diye kontrol et
            var storyNodesMap = repository.getLocalStoryNodes(storyDocId)

            // Eğer lokalde yoksa (kullanıcı silmişse), internetten yeniden indir
            if (storyNodesMap.isEmpty()) {
                repository.downloadAndSaveStory(storyDocId)
                // İndirdikten sonra lokalden tekrar oku
                storyNodesMap = repository.getLocalStoryNodes(storyDocId)
            }

            storyNodes = storyNodesMap
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