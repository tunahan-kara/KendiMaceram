// MainActivity.kt

package com.kendimaceram.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kendimaceram.app.data.Choice
import com.kendimaceram.app.ui.theme.KendiMaceramTheme
import com.kendimaceram.app.viewmodel.StoryViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: StoryViewModel by viewModels()
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TTS motoru aynı şekilde başlatılıyor.
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("tr", "TR")
            } else {
                Log.e("TTS", "TTS Motoru başlatılamadı!")
            }
        }

        setContent {
            KendiMaceramTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Hikaye metni değiştiğinde otomatik olarak okunmasını sağlıyoruz.
                LaunchedEffect(key1 = uiState.currentNode?.text) {
                    uiState.currentNode?.text?.let { newText ->
                        speakText(newText)
                    }
                }

                StoryScreen(
                    storyText = uiState.currentNode?.text ?: "Yükleniyor...",
                    choices = uiState.currentNode?.choices ?: emptyList(),
                    onChoiceSelected = { choiceId ->
                        viewModel.makeChoice(choiceId)
                    }
                )
            }
        }
    }

    private fun speakText(text: String) {
        // TTS'in hazır olup olmadığını kontrol etmeye artık gerek yok,
        // çünkü LaunchedEffect zaten hazır olduğunda çalışacak.
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}

@Composable
fun StoryScreen(
    storyText: String,
    choices: List<Choice>,
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
            Text(
                text = storyText,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f) // Metnin alanı doldurmasını sağlar
            )

            // Seçenekler için butonları dinamik olarak oluştur
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