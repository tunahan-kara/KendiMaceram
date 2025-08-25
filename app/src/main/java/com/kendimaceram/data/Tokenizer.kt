package com.kendimaceram.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// tokenizer.json dosyasının yapısını modellemek için
private data class TokenizerConfig(val added_tokens: List<Map<String, Any>>, val model: ModelData)
private data class ModelData(val vocab: Map<String, Int>)

class Tokenizer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vocab: Map<String, Int>
    private val idToToken: Map<Int, String>

    init {
        val jsonString = context.assets.open("tokenizer.json").bufferedReader().use { it.readText() }
        val config: TokenizerConfig = Gson().fromJson(jsonString, TokenizerConfig::class.java)
        vocab = config.model.vocab
        // ID'den tekrar karaktere dönüştürmek için ters bir harita oluşturuyoruz
        idToToken = vocab.entries.associate { (key, value) -> value to key }
    }

    // Bu modelin metin -> sayı dizisi çevirimi çok daha karmaşık.
    // Python örneği bize doğrudan sayısallaştırılmış bir metin verdiği için
    // şimdilik bu fonksiyonu basit bir hale getiriyoruz.
    // ASIL İŞLEM: Python'daki gibi önce fonemlere çevirmek gerekir.
    // Biz şimdilik test için basit bir karakter -> ID çevirimi yapacağız.
    fun tokenize(text: String): LongArray {
        val padId = vocab["<pad>"]?.toLong() ?: 0L
        val ids = mutableListOf<Long>()
        ids.add(padId) // Başlangıç pad token'ı

        // Basit karakter çevirimi
        text.lowercase().trim().forEach { char ->
            vocab[char.toString()]?.let {
                ids.add(it.toLong())
            }
        }

        ids.add(padId) // Bitiş pad token'ı
        return ids.toLongArray()
    }
}