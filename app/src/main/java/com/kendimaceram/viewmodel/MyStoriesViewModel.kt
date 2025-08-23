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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyStoriesViewModel @Inject constructor(
    private val repository: StoryRepository,
    private val localDataSource: LocalStoryDataSource
) : ViewModel() {

    private val _stories = MutableStateFlow<List<StoryMetadata>>(emptyList())
    val stories: StateFlow<List<StoryMetadata>> = _stories.asStateFlow()

    fun loadDownloadedStories() {
        _stories.value = localDataSource.getDownloadedStoryList()
    }

    fun deleteStory(storyId: String) {
        viewModelScope.launch {
            repository.deleteStory(storyId)
            loadDownloadedStories()
        }
    }
}