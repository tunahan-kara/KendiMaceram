package com.kendimaceram.app.ui.screens

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryReaderScreen(
    navController: NavController,
    storyId: String,
    storyTitle: String? = null,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val tts = remember { mutableStateOf<TextToSpeech?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var resumeOffset by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.loadStory(storyId) }

    DisposableEffect(Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            Log.d("TTS", "TTS kapatıldı.")
        }
    }

    fun speakFromOffset(fullText: String, startOffset: Int) {
        val engine = tts.value ?: return
        val safeOffset = startOffset.coerceIn(0, fullText.length)
        val remaining = fullText.substring(safeOffset)
        if (remaining.isBlank()) return

        val utteranceId = UUID.randomUUID().toString()
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) { viewModel.clearHighlight() }
            override fun onDone(utteranceId: String?) {
                viewModel.clearHighlight()
                isPlaying = false
                resumeOffset = 0
            }
            override fun onError(utteranceId: String?) {}
            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                val globalStart = safeOffset + start
                val globalEnd = (safeOffset + end).coerceAtMost(fullText.length)
                viewModel.updateHighlightRange(IntRange(globalStart, globalEnd))
                resumeOffset = globalStart
            }
        })
        engine.speak(remaining, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    LaunchedEffect(uiState.currentNode) {
        val text = uiState.currentNode?.text ?: return@LaunchedEffect
        tts.value?.stop()
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale("tr", "TR")
                resumeOffset = 0
                isPlaying = true
                speakFromOffset(text, resumeOffset)
            } else Log.e("TTS", "TTS başlatılamadı!")
        }
    }

    val titleText = storyTitle ?: "Hikaye"

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // APP BAR: tema ile tam uyum
            CenterAlignedTopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(titleText, maxLines = 1, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = {
                        tts.value?.stop()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    val storyText = uiState.currentNode?.text.orEmpty()
                    IconButton(onClick = {
                        if (storyText.isBlank()) return@IconButton
                        val engine = tts.value
                        if (isPlaying) {
                            engine?.stop()
                            isPlaying = false
                        } else {
                            if (engine == null) {
                                tts.value = TextToSpeech(context) { status ->
                                    if (status == TextToSpeech.SUCCESS) {
                                        tts.value?.language = Locale("tr", "TR")
                                        isPlaying = true
                                        speakFromOffset(storyText, resumeOffset)
                                    } else Log.e("TTS", "TTS tekrar başlatılamadı!")
                                }
                            } else {
                                isPlaying = true
                                speakFromOffset(storyText, resumeOffset)
                            }
                        }
                    }) {
                        if (isPlaying) {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = "Duraklat",
                                tint = MaterialTheme.colorScheme.primary // GPGreen
                            )
                        } else {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Oynat",
                                tint = MaterialTheme.colorScheme.primary // GPGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                windowInsets = WindowInsets(0)
            )

            // İçerik
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    StoryScreenContent(
                        storyText = uiState.currentNode?.text ?: "Hikaye yüklenemedi.",
                        choices = uiState.currentNode?.choices ?: emptyList(),
                        highlightRange = uiState.highlightRange,
                        onChoiceSelected = { choiceId ->
                            isPlaying = false
                            resumeOffset = 0
                            tts.value?.stop()
                            viewModel.makeChoice(choiceId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryScreenContent(
    storyText: String,
    choices: List<Choice>,
    highlightRange: IntRange?,
    onChoiceSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    LaunchedEffect(highlightRange) {
        if (highlightRange != null && textLayoutResult != null) {
            val line = textLayoutResult!!.getLineForOffset(highlightRange.first)
            val lineTopY = textLayoutResult!!.getLineTop(line) - 100
            coroutineScope.launch { scrollState.animateScrollTo(lineTopY.toInt().coerceAtLeast(0)) }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
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
                        start = highlightRange.first.coerceAtLeast(0),
                        end = highlightRange.last.coerceAtMost(storyText.length)
                    )
                }
            }
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                onTextLayout = { textLayoutResult = it }
            )
            Spacer(modifier = Modifier.height(100.dp))
            choices.forEach { choice ->
                Button(
                    onClick = { onChoiceSelected(choice.nextNodeId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) { Text(text = choice.text, modifier = Modifier.padding(8.dp)) }
            }
        }
    }
}
