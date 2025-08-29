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

// Data class'ımıza imageUrl alanını ekliyoruz
data class StoryListItemState(
    val id: String,
    val title: String,
    val imageUrl: String?, // <-- YENİ EKLENEN SATIR
    val isInLibrary: Boolean
)

data class NewStoriesUiState(
    val stories: List<StoryListItemState> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class NewStoriesViewModel @Inject constructor(
    private val repository: StoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewStoriesUiState())
    val uiState: StateFlow<NewStoriesUiState> = _uiState.asStateFlow()

    init {
        loadAllStories()
    }

    private fun loadAllStories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val allStories = repository.getAllStoriesMetadataFromFirestore()
            val libraryStoryIds = repository.getLibraryStoryIds().toSet()

            val storyList = allStories.map { metadata ->
                StoryListItemState(
                    id = metadata.id,
                    title = metadata.title,
                    imageUrl = metadata.imageUrl, // <-- YENİ EKLENEN SATIR
                    isInLibrary = metadata.id in libraryStoryIds
                )
            }
            _uiState.update { it.copy(stories = storyList, isLoading = false) }
        }
    }

    fun addStoryToUserLibrary(storyId: String) {
        viewModelScope.launch {
            repository.addStoryToLibrary(storyId)
            _uiState.update { currentState ->
                val updatedStories = currentState.stories.map { story ->
                    if (story.id == storyId) {
                        story.copy(isInLibrary = true)
                    } else {
                        story
                    }
                }
                currentState.copy(stories = updatedStories)
            }
        }
    }
}