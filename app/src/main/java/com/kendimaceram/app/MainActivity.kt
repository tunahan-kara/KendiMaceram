package com.kendimaceram.app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kendimaceram.app.data.Choice
import com.kendimaceram.app.ui.theme.KendiMaceramTheme
import com.kendimaceram.app.viewmodel.StoryViewModel
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {

    // ViewModel'i en doğru şekilde oluşturuyoruz.
    private val viewModel: StoryViewModel by viewModels()
    // TTS motoru için değişkenimiz.
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TTS motorunu başlatıyoruz.
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("tr", "TR")

                // TTS hazır olduğunda, kelime takibi yapacak olan dinleyicimizi ayarlıyoruz.
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        // Konuşma başladığında eski vurguyu temizle.
                        runOnUiThread { viewModel.clearHighlight() }
                    }

                    override fun onDone(utteranceId: String?) {
                        // Konuşma bittiğinde vurguyu temizle.
                        runOnUiThread { viewModel.clearHighlight() }
                    }

                    override fun onError(utteranceId: String?) {
                        // Hata durumunda da vurguyu temizleyebiliriz.
                        runOnUiThread { viewModel.clearHighlight() }
                    }

                    // İşte sihirli metodumuz: Bir kelime okunmaya başlamadan hemen önce tetiklenir.
                    override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                        val range = IntRange(start, end)
                        // ViewModel'e yeni vurgulanacak aralığı bildiriyoruz.
                        // Bu işlem arka planda bir thread'de çalıştığı için UI'ı ana thread'de güncellemeliyiz.
                        runOnUiThread { viewModel.updateHighlightRange(range) }
                    }
                })
            } else {
                Log.e("TTS", "TTS Motoru başlatılamadı!")
            }
        }

        setContent {
            KendiMaceramTheme {
                // ViewModel'deki durumu (state) dinliyoruz.
                val uiState by viewModel.uiState.collectAsState()

                // Bu blok, 'key1' olarak verilen değer her değiştiğinde çalışır.
                // Yani, hikayede yeni bir adıma geçildiğinde, yeni metni otomatik okur.
                LaunchedEffect(key1 = uiState.currentNode) {
                    uiState.currentNode?.text?.let { newText ->
                        speakText(newText)
                    }
                }

                // Ana ekranımızı en güncel verilerle çağırıyoruz.
                StoryScreen(
                    storyText = uiState.currentNode?.text ?: "Yükleniyor...",
                    choices = uiState.currentNode?.choices ?: emptyList(),
                    highlightRange = uiState.highlightRange,
                    onChoiceSelected = { choiceId ->
                        viewModel.makeChoice(choiceId)
                    }
                )
            }
        }
    }

    private fun speakText(text: String) {
        // UtteranceProgressListener'ın çalışabilmesi için her konuşmaya eşsiz bir kimlik vermemiz şart.
        val utteranceId = UUID.randomUUID().toString()
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onDestroy() {
        // Activity yok olurken TTS motorunu kapatmayı unutmuyoruz!
        super.onDestroy()
        tts.stop()
        tts.shutdown()
    }
}

// Arayüzümüzü oluşturan Composable fonksiyon.
@Composable
fun StoryScreen(
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
            // Metni, farklı stiller uygulayabileceğimiz AnnotatedString ile oluşturuyoruz.
            val annotatedText = buildAnnotatedString {
                append(storyText)
                if (highlightRange != null) {
                    // Eğer vurgulanacak bir aralık varsa, o aralığa özel bir stil ekliyoruz.
                    addStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.onPrimary, // Metin rengi
                            background = MaterialTheme.colorScheme.primary // Arkaplan rengi
                        ),
                        start = highlightRange.first,
                        end = highlightRange.last
                    )
                }
            }

            Text(
                text = annotatedText, // Ekranda artık bu stilli metni gösteriyoruz.
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f) // Metnin mümkün olduğunca çok yer kaplamasını sağlar.
            )

            // Seçenek butonlarını dinamik olarak oluşturuyoruz.
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