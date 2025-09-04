package com.kendimaceram.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.BuildConfig
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun HelpScreen(navController: NavController) {
    val faqs = remember {
        listOf(
            "Hikayeleri nasıl kütüphaneme eklerim?" to
                    "Keşfet sekmesinden bir hikayeye gir ve ‘Kütüphaneye Ekle’ butonuna dokun. Eklediğin hikayeleri ‘Hikayelerim’ sekmesinde bulabilirsin.",
            "Hikayelerimi nerede görürüm?" to
                    "Alt bardaki ‘Hikayelerim’ sekmesinde, kütüphanene eklediğin tüm hikayeleri görebilirsin.",
            "Okumaya nasıl başlarım?" to
                    "Hikaye detay sayfasındaki ‘Okumaya Başla’ butonuna dokun. Okuma ekranında üst çubuktaki oynat/duraklat ile sesli okumayı yönetebilirsin.",
            "Metin vurgulaması nasıl çalışır?" to
                    "Sesli okuma sırasında okunan kelimeler anlık olarak vurgulanır ve metin otomatik olarak ilgili satıra kaydırılır.",
            "Premium neler sağlar?" to
                    "Reklamsız deneyim, tüm hikayelere sınırsız erişim ve gelecekte çevrimdışı okuma gibi avantajlar. Detaylar için Premium sekmesine göz at."
        )
    }

    MainScaffold(navController = navController) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sıkça Sorulan Sorular",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            items(faqs.size) { i ->
                val (q, a) = faqs[i]
                FaqCard(question = q, answer = a)
            }

            item { Spacer(Modifier.height(8.dp)) }

            item {
                SupportCard()
            }
        }
    }
}

/* --- Pieces --- */

@Composable
private fun FaqCard(
    question: String,
    answer: String
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { expanded = !expanded },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(160)) + fadeIn(tween(160)),
                exit = shrinkVertically(animationSpec = tween(160)) + fadeOut(tween(120))
            ) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun SupportCard() {
    val uri = LocalUriHandler.current
    val mailto = remember {
        // Kullanıcı e-posta uygulamasını açar
        "mailto:destek@kendimaceram.com?subject=Kendi%20Maceram%20Destek"
    }

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MailOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Destek",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Sorun, öneri veya geri bildirimlerin için bize ulaş:",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { uri.openUri(mailto) }) {
                    Text("E-posta Gönder")
                }
                AssistChip(
                    onClick = {},
                    label = { Text("destek@kendimaceram.com") },
                    enabled = false
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Uygulama Sürümü: ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
