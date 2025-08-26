// data/LocalStoryDataSource.kt
package com.kendimaceram.app.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class StoryMetadata(
    val id: String,
    val title: String,
    val imageUrl: String? = null
)

@Singleton
class LocalStoryDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val storiesDir = File(context.filesDir, "stories")

    init {
        if (!storiesDir.exists()) {
            storiesDir.mkdirs()
        }
    }

    fun saveStory(storyId: String, fullStoryData: Map<String, Any>) {
        val file = File(storiesDir, "$storyId.json")
        val jsonString = gson.toJson(fullStoryData)
        file.writeText(jsonString)
    }

    fun getDownloadedStoryList(): List<StoryMetadata> {
        return storiesDir.listFiles { _, name -> name.endsWith(".json") }
            ?.mapNotNull { file ->
                try {
                    val jsonString = file.readText()
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    val data: Map<String, Any> = gson.fromJson(jsonString, type)
                    val title = data["title"] as? String ?: "Başlıksız Hikaye"
                    val id = file.nameWithoutExtension
                    StoryMetadata(id, title)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
    }

    fun getStory(storyId: String): Map<String, Any>? {
        val file = File(storiesDir, "$storyId.json")
        if (!file.exists()) return null
        return try {
            val jsonString = file.readText()
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            null
        }
    }


    // silme işlemini yapacak fonksiyon
    fun deleteStory(storyId: String): Boolean {
        val file = File(storiesDir, "$storyId.json")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

}