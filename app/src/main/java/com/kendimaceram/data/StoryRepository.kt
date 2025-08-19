// data/StoryRepository.kt
package com.kendimaceram.app.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val localDataSource: LocalStoryDataSource
) {
    private val db = Firebase.firestore

    // Bu fonksiyon Firestore'dan veriyi çeker ve LocalStoryDataSource'u kullanarak telefona kaydeder.
    suspend fun downloadAndSaveStory(storyDocId: String) {
        try {
            val document = db.collection("stories").document(storyDocId).get().await()
            document.data?.let { data ->
                localDataSource.saveStory(storyDocId, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Bu fonksiyon telefondaki (lokal) hikaye dosyasını okur ve kodun anlayacağı formata çevirir.
    suspend fun getLocalStoryNodes(storyId: String): Map<String, StoryNode> {
        val storyData = localDataSource.getStory(storyId) ?: return emptyMap()
        val storyNodesMap = mutableMapOf<String, StoryNode>()
        try {
            @Suppress("UNCHECKED_CAST")
            val nodesData = storyData["nodes"] as? Map<String, Map<String, Any>> ?: return emptyMap()
            for ((nodeId, nodeData) in nodesData) {
                val id = nodeData["id"] as? String ?: ""
                val text = nodeData["text"] as? String ?: ""
                @Suppress("UNCHECKED_CAST")
                val choicesData = nodeData["choices"] as? List<Map<String, String>> ?: emptyList()
                val choices = choicesData.map { choiceMap ->
                    Choice(text = choiceMap["text"] ?: "", nextNodeId = choiceMap["nextNodeId"] ?: "")
                }
                storyNodesMap[nodeId] = StoryNode(id, text, choices)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return storyNodesMap
    }
}