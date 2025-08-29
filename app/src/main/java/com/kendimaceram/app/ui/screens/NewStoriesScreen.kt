package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.components.StoryCard
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.NewStoriesViewModel
import com.kendimaceram.app.viewmodel.StoryListItemState
import kotlinx.coroutines.launch

@Composable
fun NewStoriesScreen(
    navController: NavController,
    viewModel: NewStoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MainScaffold(
        navController = navController,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.stories, key = { it.id }) { story ->
                        StoryCard(
                            title = story.title,
                            // DEĞİŞİKLİK BURADA: Artık null yerine gerçek imageUrl'ü veriyoruz
                            imageUrl = story.imageUrl,
                            storyId = story.id,
                            onClick = {
                                navController.navigate(Screen.StoryDetail.createRoute(story.id))
                            },
                            buttonContent = {
                                if (story.isInLibrary) {
                                    Text(
                                        text = "Kütüphanede ✅",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                                    )
                                } else {
                                    TextButton(
                                        onClick = {
                                            viewModel.addStoryToUserLibrary(story.id)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "'${story.title}' kütüphaneye eklendi.",
                                                    withDismissAction = true
                                                )
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Kütüphaneye Ekle")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}