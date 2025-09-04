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
import com.kendimaceram.app.viewmodel.MyStoriesViewModel

@Composable
fun MyStoriesScreen(
    navController: NavController,
    viewModel: MyStoriesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadLibrary() }
    val uiState by viewModel.uiState.collectAsState()

    MainScaffold(navController = navController) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                uiState.stories.isEmpty() -> {
                    EmptyLibrary(
                        onExplore = { navController.navigate(Screen.NewStories.route) }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.stories, key = { it.metadata.id }) { item ->
                            LibraryCardSimple(
                                title = item.metadata.title,
                                imageUrl = item.metadata.imageUrl,
                                onClick = {
                                    navController.navigate(
                                        Screen.StoryDetail.createRoute(item.metadata.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------- UI parçaları ---------- */

@Composable
private fun LibraryCardSimple(
    title: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clip(shape)
            .shadow(4.dp, shape)
            .clickable { onClick() }
    ) {
        // kapak
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl ?: "https://picsum.photos/seed/${title.hashCode()}/600/800")
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // alt gradient overlay (başlık okunaklı kalsın)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                    )
                )
        )

        // başlık
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        )
    }
}

@Composable
private fun EmptyLibrary(onExplore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Kütüphanen boş görünüyor",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Keşfet’ten yeni hikayeler ekleyebilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onExplore,
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Keşfet’e Git")
        }
    }
}
