// viewmodel/NewStoriesViewModel.kt
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

// UI'ın listeleyeceği her bir elemanın durumunu tutan data class
data class StoryListItemState(
    val id: String,
    val title: String,
    val isDownloaded: Boolean
)

// Ekranın genel durumunu tutan data class
data class AllStoriesUiState(
    val stories: List<StoryListItemState> = emptyList(),
    val isLoading: Boolean = false,
    val downloadingStoryId: String? = null // Hangi hikayenin indirildiğini takip etmek için
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

            // 1. İnternetten tüm hikayelerin listesini çek
            val remoteStories = repository.getAllStoriesMetadataFromFirestore()
            // 2. Telefondaki indirilmiş hikayelerin listesini çek
            val localStories = localDataSource.getDownloadedStoryList()
            val localStoryIds = localStories.map { it.id }.toSet()

            // 3. İki listeyi karşılaştırarak son listeyi oluştur
            val combinedList = remoteStories.map { remoteStory ->
                StoryListItemState(
                    id = remoteStory.id,
                    title = remoteStory.title,
                    isDownloaded = remoteStory.id in localStoryIds
                )
            }

            _uiState.update { it.copy(stories = combinedList, isLoading = false) }
        }
    }

    fun downloadStory(storyId: String) {
        viewModelScope.launch {
            // İndirme başladığında hangi hikayenin indirildiğini state'e bildir
            _uiState.update { it.copy(downloadingStoryId = storyId) }
            repository.downloadAndSaveStory(storyId)
            // İndirme bittiğinde listeyi yenile ve indirme durumunu sıfırla
            loadAllStories()
            _uiState.update { it.copy(downloadingStoryId = null) }
        }
    }
}