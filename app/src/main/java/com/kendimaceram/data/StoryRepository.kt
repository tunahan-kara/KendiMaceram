package com.kendimaceram.app.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object StoryRepository {
    private val db = Firebase.firestore

    // Bu fonksiyon artık Firestore'dan canlı veri çeker.
    suspend fun fetchStoryNodes(storyDocId: String): Map<String, StoryNode> {
        val storyNodesMap = mutableMapOf<String, StoryNode>()
        try {
            val document = db.collection("stories").document(storyDocId).get().await()
            @Suppress("UNCHECKED_CAST")
            val nodesData = document.get("nodes") as? Map<String, Map<String, Any>> ?: return emptyMap()

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