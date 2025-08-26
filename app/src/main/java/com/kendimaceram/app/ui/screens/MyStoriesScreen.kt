package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
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

    MainScaffold(navController = navController) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 8.dp)) {
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
                // Dikey liste yerine, 2 sütunlu bir ızgara kullanıyoruz
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.stories, key = { it.metadata.id }) { story ->
                        StoryCardItem(
                            story = story,
                            // Tıklandığında artık direkt okuyucuya değil, detay ekranına gidiyor
                            onClick = {
                                navController.navigate(Screen.StoryDetail.createRoute(story.metadata.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

// Her bir hikaye afişini temsil eden yeni bileşenimiz
@Composable
fun StoryCardItem(
    story: LibraryItemState,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Hikaye kapak resmi
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    // Şimdilik her hikayeye farklı bir rastgele resim atıyoruz
                    .data(story.metadata.imageUrl ?: "https://picsum.photos/seed/${story.metadata.id}/400/500")
                    .crossfade(true)
                    .build(),
                contentDescription = story.metadata.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f), // Afiş oranını koru (3:4)
                contentScale = ContentScale.Crop
            )
            // Hikaye adı
            Text(
                text = story.metadata.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}