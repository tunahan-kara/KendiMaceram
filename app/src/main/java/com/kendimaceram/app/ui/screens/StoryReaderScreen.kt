package com.kendimaceram.app.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.data.Choice
import com.kendimaceram.app.data.StoryMetadata
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.StoryViewModel
import java.util.*

@Composable
fun StoryReaderScreen(
    navController: NavController,
    storyId: String,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // TTS motorunu Composable içinde güvenli bir şekilde tutmak için
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    // Ekran açıldığında hikayeyi yüklemesi için ViewModel'i tetikle
    LaunchedEffect(key1 = Unit) {
        viewModel.loadStory(storyId)
    }

    // Bu Composable ekrandan ayrılırken (örn: geri tuşuna basınca)
    // TTS motorunu güvenle kapatır ve hafıza sızıntısını önler.
    DisposableEffect(key1 = Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            Log.d("TTS", "StoryReaderScreen'den çıkıldı, TTS motoru kapatıldı.")
        }
    }

    // Hikaye metni her değiştiğinde bu blok tetiklenir ve yeni metni okur.
    LaunchedEffect(key1 = uiState.currentNode) {
        // Sadece yeni bir hikaye metni varsa devam et
        uiState.currentNode?.text?.let { newText ->
            // Önceki TTS'i durdur ve temizle
            tts.value?.stop()
            tts.value?.shutdown()

            // Yeni bir TTS motoru oluştur
            tts.value = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.value?.language = Locale("tr", "TR")
                    // Kelime vurgulama için listener'ımızı ayarlıyoruz
                    tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) { viewModel.clearHighlight() }
                        override fun onDone(utteranceId: String?) { viewModel.clearHighlight() }
                        override fun onError(utteranceId: String?) {}
                        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                            viewModel.updateHighlightRange(IntRange(start, end))
                        }
                    })
                    // Konuşmayı başlat
                    val utteranceId = UUID.randomUUID().toString()
                    tts.value?.speak(newText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                }
                else {
                    Log.e("TTS", "Standart TTS motoru başlatılamadı!")
                }
            }
        }
    }

    // Arayüzü göster
    MainScaffold(navController = navController) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                StoryScreenContent(
                    storyText = uiState.currentNode?.text ?: "Hikaye yüklenemedi.",
                    choices = uiState.currentNode?.choices ?: emptyList(),
                    highlightRange = uiState.highlightRange,
                    onChoiceSelected = { choiceId ->
                        viewModel.makeChoice(choiceId)
                    }
                )
            }
        }
    }
}

// Bu yardımcı Composable, UI'ı çizmekten sorumlu.
@Composable
private fun StoryScreenContent(
    storyText: String,
    choices: List<Choice>,
    highlightRange: IntRange?,
    onChoiceSelected: (String) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val annotatedText = buildAnnotatedString {
                append(storyText)
                if (highlightRange != null) {
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onPrimary,
                            background = MaterialTheme.colorScheme.primary
                        ),
                        start = highlightRange.first,
                        end = highlightRange.last
                    )
                }
            }
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            choices.forEach { choice ->
                Button(
                    onClick = { onChoiceSelected(choice.nextNodeId) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = choice.text)
                }
            }
        }
    }
}