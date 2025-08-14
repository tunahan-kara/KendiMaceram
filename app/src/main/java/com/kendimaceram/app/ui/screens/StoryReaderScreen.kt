package com.kendimaceram.app.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.foundation.layout.*
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
import com.kendimaceram.app.viewmodel.StoryViewModel
import java.util.*

@Composable
fun StoryReaderScreen(
    navController: NavController,
    storyId: String,
    viewModel: StoryViewModel = hiltViewModel() // ViewModel'i en modern şekilde alıyoruz
) {
    // ViewModel'den gelen canlı durumu (state) dinliyoruz
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // TTS motorunu ve durumunu yönetmek için Composable state'ler
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    val isTtsReady = remember { mutableStateOf(false) }

    // Bu Composable ekrana geldiğinde sadece bir kere çalışır.
    LaunchedEffect(key1 = Unit) {
        viewModel.loadStory(storyId) // ViewModel'e hangi hikayeyi yükleyeceğini söylüyoruz
    }

    // Bu Composable yok olurken (ekrandan ayrılırken) TTS motorunu kapatır.
    DisposableEffect(key1 = Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
        }
    }

    // TTS motorunu kurma ve hazır hale getirme
    LaunchedEffect(key1 = context) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady.value = true
                tts.value?.language = Locale("tr", "TR")
                tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { viewModel.clearHighlight() }
                    override fun onDone(utteranceId: String?) { viewModel.clearHighlight() }
                    override fun onError(utteranceId: String?) { viewModel.clearHighlight() }
                    override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                        viewModel.updateHighlightRange(IntRange(start, end))
                    }
                })
            } else {
                Log.e("TTS", "TTS Motoru başlatılamadı!")
            }
        }
    }

    // Hikaye metni değiştiğinde ve TTS hazır olduğunda otomatik okuma
    LaunchedEffect(key1 = uiState.currentNode, key2 = isTtsReady.value) {
        if (isTtsReady.value) {
            uiState.currentNode?.text?.let { newText ->
                val utteranceId = UUID.randomUUID().toString()
                tts.value?.speak(newText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        }
    }

    // Arayüz
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        StoryScreenContent(
            storyText = uiState.currentNode?.text ?: "Hikaye Yüklenemedi.",
            choices = uiState.currentNode?.choices ?: emptyList(),
            highlightRange = uiState.highlightRange,
            onChoiceSelected = { choiceId ->
                viewModel.makeChoice(choiceId)
            }
        )
    }
}

// Arayüzü oluşturan asıl Composable (eski StoryScreen fonksiyonumuz)
@Composable
private fun StoryScreenContent(
    storyText: String,
    choices: List<Choice>,
    highlightRange: IntRange?,
    onChoiceSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = choice.text)
                }
            }
        }
    }
}