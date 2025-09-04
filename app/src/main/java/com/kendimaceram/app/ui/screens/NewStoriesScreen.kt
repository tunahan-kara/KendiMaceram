package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.NewStoriesViewModel
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
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.stories, key = { it.id }) { story ->
                        StoryGridCard(
                            title = story.title,
                            imageUrl = story.imageUrl,
                            isInLibrary = story.isInLibrary,
                            onClick = {
                                navController.navigate(Screen.StoryDetail.createRoute(story.id))
                            },
                            onAdd = {
                                viewModel.addStoryToUserLibrary(story.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "'${story.title}' kütüphaneye eklendi.",
                                        withDismissAction = true
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryGridCard(
    title: String,
    imageUrl: String?,
    isInLibrary: Boolean,
    onClick: () -> Unit,
    onAdd: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // dikdörtgen oranda kart
            .clip(shape)
            .shadow(elevation = 4.dp, shape = shape)
            .clickable { onClick() }
    ) {
        // arka plan görsel
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: "https://picsum.photos/400/600")
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // alt gradient overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                    )
                )
        )

        // alt yazılar
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 2
            )
            Spacer(Modifier.height(6.dp))
            if (isInLibrary) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = "Kütüphanede ✅",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            } else {
                TextButton(
                    onClick = onAdd,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Kütüphaneye Ekle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
