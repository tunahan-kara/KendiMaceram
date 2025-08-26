package com.kendimaceram.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Hikaye detaylarını tutacak olan data class
data class StoryDetailState(
    val title: String = "Yükleniyor...",
    val summary: String = "",
    val imageUrl: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val repository: StoryRepository,
    savedStateHandle: SavedStateHandle // Navigasyondan gelen argümanları (storyId) yakalamak için
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoryDetailState())
    val uiState = _uiState.asStateFlow()

    private val storyId: String = checkNotNull(savedStateHandle["storyId"])

    init {
        loadStoryDetails()
    }

    private fun loadStoryDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Not: Bu fonksiyonu StoryRepository'ye eklememiz gerekecek.
            val storyData = repository.getStoryDetailsFromFirestore(storyId)

            if (storyData != null) {
                _uiState.update {
                    it.copy(
                        title = storyData.title,
                        summary = storyData.summary,
                        imageUrl = storyData.imageUrl,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        title = "Hikaye Bulunamadı",
                        isLoading = false
                    )
                }
            }
        }
    }
}