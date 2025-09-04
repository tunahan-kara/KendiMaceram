package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.kendimaceram.app.viewmodel.StoryDetailViewModel

@Composable
fun StoryDetailScreen(
    navController: NavController,
    storyId: String,
    viewModel: StoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Tema kısayolları
    val bg = MaterialTheme.colorScheme.background
    val onBg = MaterialTheme.colorScheme.onBackground
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    // Görsel kapsayıcısının köşesi (yumuşak)
    val coverShape = RoundedCornerShape(16.dp)

    // Alt kısımda görsel → arka plan yumuşak geçiş için scrim
    val bottomScrim = Brush.verticalGradient(
        0f to Color.Transparent,
        0.45f to bg.copy(alpha = 0.15f),   // çok hafif koyulaştır
        0.75f to bg.copy(alpha = 0.55f),
        1f to bg                           // sona doğru tamamen tema arka planı
    )

    MainScaffold(navController = navController) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // --- KAPAK GÖRSELİ + SCRIM ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(coverShape)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uiState.imageUrl ?: "https://picsum.photos/seed/$storyId/1000/700")
                            .crossfade(true)
                            .build(),
                        contentDescription = uiState.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )
                    // Alt scrim – açık görselleri temaya bağlar
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(bottomScrim)
                    )
                    // Çok ince çerçeve (tema ile uyumlu, opsiyonel)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(coverShape)
                            .background(Color.Transparent)
                            .border(
                                width = 0.5.dp,
                                color = surfaceVariant.copy(alpha = 0.3f),
                                shape = coverShape
                            )
                    )
                }

                // --- DETAY BLOĞU ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = uiState.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = onBg
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = uiState.summary,
                        style = MaterialTheme.typography.bodyLarge,
                        color = onBg.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { navController.navigate(Screen.StoryReader.createRoute(storyId)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primary,
                            contentColor = onPrimary
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Okumaya Başla", modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}
