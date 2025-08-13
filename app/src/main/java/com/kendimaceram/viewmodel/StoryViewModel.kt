// StoryViewModel.kt DOSYASININ TAM İÇERİĞİ

package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kendimaceram.app.data.StoryNode
import com.kendimaceram.app.data.StoryRepository
import com.kendimaceram.app.data.StoryUiState // <-- Android Studio bu import'u otomatik eklemeli
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class StoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()

    init {
        startStory()
    }

    private fun startStory() {
        val startNode = StoryRepository.getNode("start")
        _uiState.update { it.copy(currentNode = startNode) }
    }

    fun makeChoice(choiceNodeId: String) {
        val nextNode = StoryRepository.getNode(choiceNodeId)
        _uiState.update { it.copy(currentNode = nextNode) }
    }
}