package com.kendimaceram.app.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepository @Inject constructor(
    private val localDataSource: LocalStoryDataSource
) {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // Sadece kullanıcının sahip olduğu (kütüphanesine eklediği) hikayelerin ID'lerini döner.
    suspend fun getLibraryStoryIds(): List<String> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val document = db.collection("users").document(userId).get().await()
            (document.get("downloadedStories") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Bir hikayeyi sadece kullanıcının kütüphanesine (Firebase'e) ekler.
    suspend fun addStoryToLibrary(storyId: String) {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)
        try {
            // FieldValue.arrayUnion, eğer listede zaten varsa tekrar eklemez.
            userDocRef.update("downloadedStories", FieldValue.arrayUnion(storyId)).await()
        } catch (e: Exception) {
            // Döküman henüz yoksa, oluştur ve ilk elemanı ekle.
            db.collection("users").document(userId).set(mapOf("downloadedStories" to listOf(storyId))).await()
        }
    }

    // Sahip olunan bir hikayeyi internetten indirip telefon hafızasına kaydeder.
    suspend fun downloadStoryToLocal(storyDocId: String): Boolean {
        return try {
            val document = db.collection("stories").document(storyDocId).get().await()
            document.data?.let { data ->
                localDataSource.saveStory(storyDocId, data)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Bir hikayeyi SADECE telefon hafızasından siler.
    suspend fun deleteStoryFromDevice(storyId: String) {
        localDataSource.deleteStory(storyId)
    }

    // Firestore'daki tüm hikayelerin genel listesini çeker.
    suspend fun getAllStoriesMetadataFromFirestore(): List<StoryMetadata> {
        return try {
            val documents = db.collection("stories").get().await()
            documents.map { doc ->
                StoryMetadata(
                    id = doc.id,
                    title = doc.getString("title") ?: "Başlıksız Hikaye"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Telefon hafızasındaki tek bir hikayenin içeriğini okur.
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