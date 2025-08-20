// ui/screens/NewStoriesScreen.kt
package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.NewStoriesViewModel
import com.kendimaceram.app.viewmodel.StoryListItemState

@Composable
fun NewStoriesScreen(
    navController: NavController,
    viewModel: NewStoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScaffold(navController = navController) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.stories) { story ->
                        AllStoriesListItem(
                            story = story,
                            isDownloading = uiState.downloadingStoryId == story.id,
                            onDownloadClick = { viewModel.downloadStory(story.id) },
                            onOpenClick = { navController.navigate(Screen.StoryReader.createRoute(story.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AllStoriesListItem(
    story: StoryListItemState,
    isDownloading: Boolean,
    onDownloadClick: () -> Unit,
    onOpenClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = story.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))

            if (story.isDownloaded) {
                Button(onClick = onOpenClick) {
                    Text("Aç")
                }
            } else {
                Button(onClick = onDownloadClick, enabled = !isDownloading) {
                    if (isDownloading) {
                        // DÜZELTME BURADA: SizedBox yerine Box kullandık.
                        Box(modifier = Modifier.size(24.dp)) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp // Daha ince bir görünüm için
                            )
                        }
                    } else {
                        Text("İndir")
                    }
                }
            }
        }
    }
}