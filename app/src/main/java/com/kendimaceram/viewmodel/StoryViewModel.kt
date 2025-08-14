package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kendimaceram.app.data.StoryNode
import com.kendimaceram.app.data.StoryRepository
import com.kendimaceram.app.data.StoryUiState
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

    // Kullanıcı bir seçim yaptığında bu fonksiyon çağrılacak.
    fun makeChoice(choiceNodeId: String) {
        val nextNode = StoryRepository.getNode(choiceNodeId)
        // Yeni bir seçime geçildiğinde eski vurguyu temizliyoruz.
        _uiState.update { it.copy(currentNode = nextNode, highlightRange = null) }
    }

    // YENİ EKLENEN FONKSİYON 1: Vurgulanacak aralığı güncellemek için.
    // MainActivity'nin aradığı fonksiyonlardan biri bu.
    fun updateHighlightRange(range: IntRange) {
        _uiState.update { it.copy(highlightRange = range) }
    }

    // YENİ EKLENEN FONKSİYON 2: Vurguyu temizlemek için.
    // MainActivity'nin aradığı diğer fonksiyon da bu.
    fun clearHighlight() {
        _uiState.update { it.copy(highlightRange = null) }
    }
}