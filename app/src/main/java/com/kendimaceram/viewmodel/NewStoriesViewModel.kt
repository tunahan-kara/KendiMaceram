package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.LocalStoryDataSource
import com.kendimaceram.app.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// EKSİK OLAN PARÇA 1: UI'ın listeleyeceği her bir elemanın durumunu tutan data class
data class StoryListItemState(
    val id: String,
    val title: String,
    val isDownloaded: Boolean
)

// EKSİK OLAN PARÇA 2: Ekranın genel durumunu tutan data class
data class AllStoriesUiState(
    val stories: List<StoryListItemState> = emptyList(),
    val isLoading: Boolean = false,
    val downloadingStoryId: String? = null
)

@HiltViewModel
class NewStoriesViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val localDataSource: LocalStoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllStoriesUiState())
    val uiState: StateFlow<AllStoriesUiState> = _uiState.asStateFlow()

    init {
        loadAllStories()
    }

    fun loadAllStories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. İnternetten tüm hikayelerin genel listesini çek
            val allAvailableStories = repository.getAllStoriesMetadataFromFirestore()

            // 2. Telefonun hafızasında GERÇEKTEN yüklü olan hikayelerin ID'lerini al
            val locallyPresentStoryIds = localDataSource.getDownloadedStoryList().map { it.id }.toSet()

            // 3. Bu iki bilgiyi karşılaştırarak son listeyi oluştur
            val combinedList = allAvailableStories.map { story ->
                StoryListItemState(
                    id = story.id,
                    title = story.title,
                    // "isDownloaded" durumu artık sadece telefonda dosya varsa true olacak.
                    isDownloaded = story.id in locallyPresentStoryIds
                )
            }

            _uiState.update { it.copy(stories = combinedList, isLoading = false) }
        }
    }

    fun downloadStory(storyId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadingStoryId = storyId) }
            repository.downloadAndSaveStory(storyId)
            // İndirme sonrası listeyi yenileyerek butonun "Aç" olmasını sağla
            loadAllStories()
            _uiState.update { it.copy(downloadingStoryId = null) }
        }
    }
}