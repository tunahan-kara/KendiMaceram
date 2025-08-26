package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.LibraryItemState
import com.kendimaceram.app.viewmodel.MyStoriesViewModel

@Composable
fun MyStoriesScreen(
    navController: NavController,
    viewModel: MyStoriesViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadLibrary()
    }
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var storyToDelete by remember { mutableStateOf<LibraryItemState?>(null) }

    MainScaffold(navController = navController) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.stories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Kütüphanenizde hiç hikaye yok.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.stories, key = { it.metadata.id }) { story ->
                        LibraryListItem(
                            story = story,
                            onOpenClick = {
                                navController.navigate(Screen.StoryReader.createRoute(story.metadata.id))
                            },
                            onDownloadClick = {
                                viewModel.downloadStory(story.metadata.id)
                            },
                            onDeleteClick = {
                                storyToDelete = story
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDialog && storyToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hikayeyi Cihazdan Sil") },
            text = { Text("'${storyToDelete?.metadata?.title}' adlı hikayeyi cihazınızdan silmek istediğinizden emin misiniz? Bu işlem hikayeyi kütüphanenizden kaldırmaz.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStoryFromDevice(storyToDelete!!.metadata.id)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun LibraryListItem(
    story: LibraryItemState,
    onOpenClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = story.metadata.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (story.isDownloaded) "Cihaza İndirildi" else "Bulutta",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            if (story.isDownloaded) {
                // Eğer indirilmişse, "Aç" butonu ve "Sil" ikonu göster
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onOpenClick) {
                        Text("Aç")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Cihazdan Sil")
                    }
                }
            } else {
                // İndirilmemişse, "İndir" butonu göster
                Button(onClick = onDownloadClick, enabled = !story.isDownloading) {
                    if (story.isDownloading) {
                        Box(modifier = Modifier.size(24.dp)) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        }
                    } else {
                        Text("İndir")
                    }
                }
            }
        }
    }
}