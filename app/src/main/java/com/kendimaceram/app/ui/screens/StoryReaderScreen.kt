package com.kendimaceram.app.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.data.Choice
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.viewmodel.StoryViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun StoryReaderScreen(
    navController: NavController,
    storyId: String,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadStory(storyId)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            Log.d("TTS", "StoryReaderScreen'den çıkıldı, TTS motoru kapatıldı.")
        }
    }

    LaunchedEffect(key1 = uiState.currentNode) {
        uiState.currentNode?.text?.let { newText ->
            tts.value?.stop()
            tts.value?.shutdown()

            tts.value = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts.value?.language = Locale("tr", "TR")
                    tts.value?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) { viewModel.clearHighlight() }
                        override fun onDone(utteranceId: String?) { viewModel.clearHighlight() }
                        override fun onError(utteranceId: String?) {}
                        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                            viewModel.updateHighlightRange(IntRange(start, end))
                        }
                    })
                    val utteranceId = UUID.randomUUID().toString()
                    tts.value?.speak(newText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
                } else {
                    Log.e("TTS", "Standart TTS motoru başlatılamadı!")
                }
            }
        }
    }

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

// BU YARDIMCI COMPOSABLE'I OTOMATİK KAYDIRMA İÇİN GÜNCELLİYORUZ
@Composable
private fun StoryScreenContent(
    storyText: String,
    choices: List<Choice>,
    highlightRange: IntRange?,
    onChoiceSelected: (String) -> Unit
) {
    // 1. Kaydırma durumunu ve coroutine scope'u hatırlıyoruz
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // 2. highlightRange her değiştiğinde bu blok çalışır
    LaunchedEffect(highlightRange) {
        if (highlightRange != null && textLayoutResult != null) {
            // Parlayan kelimenin satır numarasını bul
            val line = textLayoutResult!!.getLineForOffset(highlightRange.first)
            // O satırın ekrandaki Y pozisyonunu bul (biraz üstten boşluk bırakalım)
            val lineTopY = textLayoutResult!!.getLineTop(line) - 100

            // Ekranda o pozisyona doğru yumuşak bir şekilde kaydır
            coroutineScope.launch {
                scrollState.animateScrollTo(lineTopY.toInt().coerceAtLeast(0))
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                // 3. Column'a dikey kaydırma özelliği ekliyoruz
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally // Butonları ortalamak için
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
                style = MaterialTheme.typography.headlineSmall, // Yazıyı biraz küçülttük
                textAlign = TextAlign.Center,
                // 4. Metnin layout bilgisi hazır olduğunda yakalıyoruz
                onTextLayout = { textLayoutResult = it }
            )

            // Metin ve butonlar arasına boşluk koyarak ayrılmalarını sağlıyoruz
            Spacer(modifier = Modifier.height(100.dp))

            choices.forEach { choice ->
                Button(
                    onClick = { onChoiceSelected(choice.nextNodeId) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(text = choice.text, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}