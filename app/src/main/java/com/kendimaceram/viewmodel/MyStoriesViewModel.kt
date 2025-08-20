// MyStoriesViewModel.kt
package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kendimaceram.app.data.LocalStoryDataSource
import com.kendimaceram.app.data.StoryMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyStoriesViewModel @Inject constructor(
    private val localStoryDataSource: LocalStoryDataSource
) : ViewModel() {

    private val _stories = MutableStateFlow<List<StoryMetadata>>(emptyList())
    val stories: StateFlow<List<StoryMetadata>> = _stories.asStateFlow()

    init {
        loadDownloadedStories()
    }

    fun loadDownloadedStories() {
        _stories.value = localStoryDataSource.getDownloadedStoryList()
    }

    // YENİ FONKSİYON
    fun deleteStory(storyId: String) {
        val isDeleted = localStoryDataSource.deleteStory(storyId)
        if (isDeleted) {
            // Silme işlemi başarılıysa, ekrandaki listeyi anında güncellemek için
            // listeyi yeniden yükle.
            loadDownloadedStories()
        }
    }

}