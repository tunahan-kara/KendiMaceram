package com.kendimaceram.app.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.LongBuffer
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "KokoroTtsEngine"

@Singleton
class OnDeviceTtsEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val ortEnvironment: OrtEnvironment = OrtEnvironment.getEnvironment()
    private lateinit var ttsSession: OrtSession
    private lateinit var tokenizer: Tokenizer
    private var audioTrack: AudioTrack? = null

    var isInitialized = false
        private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Kokoro TTS Modeli yükleniyor...")
                ttsSession = createSession("model_q8f16.onnx")
                Log.d(TAG, "✅ Kokoro modeli yüklendi.")
                Log.d(TAG, "Tokenizer başlatılıyor...")
                tokenizer = Tokenizer(context)
                Log.d(TAG, "✅ Tokenizer başlatıldı.")
                isInitialized = true
                Log.d(TAG, "🚀 Motor hazır!")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Model veya Tokenizer yüklenirken bir hata oluştu!", e)
            }
        }
    }

    private fun createSession(modelName: String): OrtSession {
        val modelBytes = context.assets.open(modelName).readBytes()
        return ortEnvironment.createSession(modelBytes)
    }

    fun synthesize(text: String) {
        if (!isInitialized) {
            Log.e(TAG, "Motor henüz hazır değil.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Sentezleme başlıyor: '$text'")

                // 1. Metni sayılara çevir (Tokenizer)
                val inputIds = tokenizer.tokenize(text)
                val inputShape = longArrayOf(1, inputIds.size.toLong())
                val inputBuffer = LongBuffer.wrap(inputIds)
                val inputTensor = OnnxTensor.createTensor(ortEnvironment, inputBuffer, inputShape)

                // 2. Ses stilini (speaker embedding) dosyadan oku
                val voiceBytes = context.assets.open("am_michael.bin").readBytes()
                val voiceBuffer = ByteBuffer.wrap(voiceBytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
                // Python'daki gibi belirli bir indeksi almak yerine, ilk ses örneğini (ilk 256 float) alıyoruz
                val styleFloats = FloatArray(256)
                voiceBuffer.get(styleFloats)
                val styleTensor = OnnxTensor.createTensor(
                    ortEnvironment,
                    FloatBuffer.wrap(styleFloats),
                    longArrayOf(1, 256)
                )

                // 3. Hız (speed) girdisini oluştur
                val speedTensor = OnnxTensor.createTensor(
                    ortEnvironment,
                    FloatBuffer.wrap(floatArrayOf(1.0f)),
                    longArrayOf(1)
                )

                // Girdileri, Python örneğindeki doğru isimlerle bir Map'e koyuyoruz.
                val inputs = mapOf(
                    "input_ids" to inputTensor,
                    "style" to styleTensor,
                    "speed" to speedTensor
                )

                // Modeli çalıştır
                val results = ttsSession.run(inputs)
                val audioData = results.use { (it.get(0) as OnnxTensor).floatBuffer.array() }

                Log.d(TAG, "✅ Ses başarıyla üretildi. Boyut: ${audioData.size}")
                playAudio(audioData)

            } catch (e: Throwable) {
                Log.e(TAG, "❌ Sentezleme sırasında hata!", e)
            }
        }
    }

    private fun playAudio(audioData: FloatArray) {
        val sampleRate = 24000 // Bu model 24kHz ile eğitilmiş
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT)
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        try {
            audioTrack?.play()
            audioTrack?.write(audioData, 0, audioData.size, AudioTrack.WRITE_BLOCKING)
        } catch (e: Exception) {
            Log.e(TAG, "❌ AudioTrack sesi çalarken hata oluştu!", e)
        }
    }
}