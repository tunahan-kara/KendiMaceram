// ui/screens/MyStoriesScreen.kt
package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.data.StoryMetadata
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.MyStoriesViewModel

@Composable
fun MyStoriesScreen(
    navController: NavController,
    viewModel: MyStoriesViewModel = hiltViewModel()
) {
    // Bu ekran her görüntülendiğinde, listenin güncel halini almasını sağlar.
    LaunchedEffect(key1 = Unit) {
        viewModel.loadDownloadedStories()
    }
    val stories by viewModel.stories.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("İndirdiğim Hikayeler", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (stories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Henüz hiç hikaye indirmemişsin.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(stories) { story ->
                    StoryListItem(story = story, onClick = {
                        // Bir hikayeye tıklandığında, ID'si ile birlikte okuma ekranına yönlendirir.
                        navController.navigate(Screen.StoryReader.createRoute(story.id))
                    })
                }
            }
        }
    }
}

@Composable
fun StoryListItem(story: StoryMetadata, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = story.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}