package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.components.StoryCard
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.MyStoriesViewModel

@Composable
fun MyStoriesScreen(
    navController: NavController,
    viewModel: MyStoriesViewModel = hiltViewModel()
) {
    // Ekran açıldığında kütüphaneyi yükle
    LaunchedEffect(key1 = Unit) {
        viewModel.loadLibrary()
    }
    // ViewModel'deki durumu (UI state) dinle
    val uiState by viewModel.uiState.collectAsState()

    // Ana iskelet yapısını (TopBar, BottomBar vb.) oluştur
    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Yüklenme durumu göstergesi
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Kütüphane boşsa uyarı mesajı göster
            else if (uiState.stories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Kütüphanenizde hiç hikaye yok.")
                }
            }
            // Kütüphanede hikaye varsa, ızgara (grid) yapısını göster
            else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    // Grid'in kendisine her yönden boşluk veriyoruz
                    contentPadding = PaddingValues(16.dp),
                    // Dikey ve yatay boşlukları artırıyoruz
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.stories, key = { it.metadata.id }) { story ->
                        StoryCard(
                            title = story.metadata.title,
                            imageUrl = story.metadata.imageUrl,
                            storyId = story.metadata.id,
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