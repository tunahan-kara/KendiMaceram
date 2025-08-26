package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.LocalStoryDataSource
import com.kendimaceram.app.data.StoryMetadata
import com.kendimaceram.app.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryItemState(
    val metadata: StoryMetadata,
    val isDownloaded: Boolean,
    val isDownloading: Boolean = false
)

data class MyStoriesUiState(
    val stories: List<LibraryItemState> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MyStoriesViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val localDataSource: LocalStoryDataSource
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyStoriesUiState())
    val uiState: StateFlow<MyStoriesUiState> = _uiState.asStateFlow()

    fun loadLibrary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. Kullanıcının kütüphanesindeki tüm hikayelerin ID'lerini al
            val libraryStoryIds = repository.getLibraryStoryIds()
            // 2. Tüm hikayelerin genel bilgilerini al
            val allStoriesMetadata = repository.getAllStoriesMetadataFromFirestore()
            // 3. Telefonda gerçekten yüklü olanların ID'lerini al
            val localStoryIds = localDataSource.getDownloadedStoryList().map { it.id }.toSet()

            // 4. Bu bilgileri birleştirerek son listeyi oluştur
            val libraryItems = libraryStoryIds.mapNotNull { id ->
                allStoriesMetadata.find { it.id == id }?.let { metadata ->
                    LibraryItemState(
                        metadata = metadata,
                        isDownloaded = metadata.id in localStoryIds
                    )
                }
            }
            _uiState.update { it.copy(stories = libraryItems, isLoading = false) }
        }
    }

    fun downloadStory(storyId: String) {
        viewModelScope.launch {
            // İndirme başladığını UI'a bildir
            _uiState.update { currentState ->
                currentState.copy(
                    stories = currentState.stories.map {
                        if (it.metadata.id == storyId) it.copy(isDownloading = true) else it
                    }
                )
            }
            repository.downloadStoryToLocal(storyId)
            // İndirme bitince listeyi yenile
            loadLibrary()
        }
    }

    fun deleteStoryFromDevice(storyId: String) {
        viewModelScope.launch {
            repository.deleteStoryFromDevice(storyId)
            loadLibrary()
        }
    }
}