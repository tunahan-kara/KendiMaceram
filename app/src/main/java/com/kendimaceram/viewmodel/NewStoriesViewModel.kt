package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoryListItemState(
    val id: String,
    val title: String,
    val isInLibrary: Boolean // İsim değişikliği
)

data class AllStoriesUiState(
    val stories: List<StoryListItemState> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class NewStoriesViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllStoriesUiState())
    val uiState: StateFlow<AllStoriesUiState> = _uiState.asStateFlow()

    init {
        loadAllStories()
    }

    fun loadAllStories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val allAvailableStories = repository.getAllStoriesMetadataFromFirestore()
            val libraryStoryIds = repository.getLibraryStoryIds().toSet()

            val combinedList = allAvailableStories.map { story ->
                StoryListItemState(
                    id = story.id,
                    title = story.title,
                    isInLibrary = story.id in libraryStoryIds
                )
            }
            _uiState.update { it.copy(stories = combinedList, isLoading = false) }
        }
    }

    fun addStoryToLibrary(storyId: String) {
        viewModelScope.launch {
            repository.addStoryToLibrary(storyId)
            loadAllStories() // Listeyi yenileyerek butonun durumunu güncelle
        }
    }
}