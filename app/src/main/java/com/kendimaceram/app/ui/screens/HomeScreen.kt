package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.HomeViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val bg = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val primary = MaterialTheme.colorScheme.primary
    val onBg = MaterialTheme.colorScheme.onBackground
    val onSurface = MaterialTheme.colorScheme.onSurface

    // Yumuşak gradient arkaplan
    val gradient = Brush.verticalGradient(
        0f to bg,
        1f to surface
    )

    // Ana iskelet
    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Başlık + Tagline
                Column {
                    Text(
                        text = "Kendi Maceram",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = onBg
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Seçimlerinle şekillenen interaktif hikayeler.",
                        style = MaterialTheme.typography.titleMedium,
                        color = onBg.copy(alpha = 0.75f)
                    )
                }

                // Hero / Öne çıkan kart
                Card(
                    colors = CardDefaults.cardColors(containerColor = surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Bugün ne keşfetmek istersin?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = onSurface
                        )
                        Text(
                            "Yeni maceralara dal veya yarım bıraktığın hikayeye dön.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onSurface.copy(alpha = 0.85f)
                        )
                        Spacer(Modifier.height(6.dp))

                        // Birincil ve ikincil büyük CTA’lar
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { navController.navigate(Screen.NewStories.route) },
                                modifier = Modifier.weight(1f).height(52.dp)
                            ) {
                                Icon(Icons.Default.Explore, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Keşfe Başla", fontSize = 16.sp)
                            }
                            OutlinedButton(
                                onClick = { navController.navigate(Screen.MyStories.route) },
                                modifier = Modifier.weight(1f).height(52.dp)
                            ) {
                                Icon(Icons.Default.AutoStories, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Hikayelerim", fontSize = 16.sp)
                            }
                        }
                    }
                }

                // Premium highlight (hafif vurgulu bar)
                Surface(
                    color = primary.copy(alpha = 0.10f),
                    contentColor = onBg,
                    shape = RoundedCornerShape(14.dp),
                    tonalElevation = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = primary)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Premium’a Geç",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = onBg
                            )
                            Text(
                                "Reklamsız, sınırsız ve çevrimdışı deneyim.",
                                style = MaterialTheme.typography.bodySmall,
                                color = onBg.copy(alpha = 0.75f)
                            )
                        }
                        TextButton(onClick = { navController.navigate(Screen.Premium.route) }) {
                            Text("Detaylar")
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Çıkış – düşük hiyerarşi (kırmızı buton baskın olmasın)
                OutlinedButton(
                    onClick = {
                        viewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                ) {
                    Text("Çıkış Yap")
                }
            }
        }
    }
}
