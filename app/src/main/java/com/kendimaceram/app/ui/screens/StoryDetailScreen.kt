package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.kendimaceram.app.viewmodel.StoryDetailViewModel

@Composable
fun StoryDetailScreen(
    navController: NavController,
    storyId: String,
    viewModel: StoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Hikaye kapak resmi (şimdilik internetten bir yer tutucu resim kullanıyoruz)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.imageUrl ?: "https://picsum.photos/seed/${storyId}/400/300")
                        .crossfade(true)
                        .build(),
                    contentDescription = uiState.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )

                // Hikaye detayları
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.summary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Butonu en alta itmek için
                    Button(
                        onClick = { navController.navigate(Screen.StoryReader.createRoute(storyId)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Yeşil renk
                    ) {
                        Text("Okumaya Başla", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}