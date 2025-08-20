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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.data.StoryMetadata
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.MyStoriesViewModel

@Composable
fun MyStoriesScreen(
    navController: NavController,
    viewModel: MyStoriesViewModel = hiltViewModel()
) {
    // --- State Yönetimi ---
    LaunchedEffect(key1 = Unit) {
        viewModel.loadDownloadedStories()
    }
    val stories by viewModel.stories.collectAsState()

    // Onay penceresinin (dialog) durumunu yönetmek için iki yeni hafıza değişkeni
    var showDialog by remember { mutableStateOf(false) }
    var storyToDelete by remember { mutableStateOf<StoryMetadata?>(null) }


    MainScaffold(navController = navController) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))

            if (stories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz hiç hikaye indirmemişsin.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(stories) { story ->
                        // HATA VEREN YERİ ŞİMDİ DÜZELTİYORUZ
                        StoryListItem(
                            story = story,
                            onClick = {
                                navController.navigate(Screen.StoryReader.createRoute(story.id))
                            },
                            // Silme ikonuna basıldığında ne yapılacağını söylüyoruz
                            onDeleteClick = {
                                storyToDelete = story // Hangi hikayeyi sileceğimizi hafızaya al
                                showDialog = true   // ve onay penceresini göster
                            }
                        )
                    }
                }
            }
        }
    }

    // --- Onay Penceresi (AlertDialog) ---
    // Sadece showDialog true olduğunda ekranda görünür.
    if (showDialog && storyToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Hikayeyi Sil") },
            text = { Text("'${storyToDelete?.title}' adlı hikayeyi silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStory(storyToDelete!!.id)
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
fun StoryListItem(
    story: StoryMetadata,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit // <-- YENİ BİR PARAMETRE EKLEDİK
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Başlık, satırda kalan tüm boşluğu kaplayacak
            Text(text = story.title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

            // Çöp Kutusu ikonu
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hikayeyi Sil")
            }
        }
    }
}
