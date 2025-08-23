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

    // Giriş yapmış kullanıcının indirdiği (yani hakkı olan) hikayelerin ID listesini Firestore'dan çeker.
    suspend fun getDownloadedStoryIdsFromFirestore(): List<String> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val document = db.collection("users").document(userId).get().await()
            (document.get("downloadedStories") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Bir hikaye indirildiğinde, ID'sini o kullanıcının Firestore'daki listesine ekler.
    private suspend fun addDownloadedStoryIdToFirestore(storyId: String) {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)
        try {
            // FieldValue.arrayUnion, eğer listede zaten varsa tekrar eklemez, bu sayede liste temiz kalır.
            userDocRef.update("downloadedStories", FieldValue.arrayUnion(storyId)).await()
        } catch (e: Exception) {
            // Eğer kullanıcı dökümanı daha önce hiç oluşturulmamışsa (ilk indirmesiyse),
            // dökümanı oluştur ve ilk elemanı ekle.
            db.collection("users").document(userId).set(mapOf("downloadedStories" to listOf(storyId))).await()
        }
    }

    // Bu fonksiyonu şimdilik kullanmıyoruz ama ileride gerekebilir diye bırakabiliriz.
    private suspend fun removeDownloadedStoryIdFromFirestore(storyId: String) {
        val userId = auth.currentUser?.uid ?: return
        try {
            val userDocRef = db.collection("users").document(userId)
            userDocRef.update("downloadedStories", FieldValue.arrayRemove(storyId)).await()
        } catch (e: Exception) {
            Log.e("Firestore", "Hikaye ID'si silinirken hata", e)
        }
    }

    // Bu fonksiyon artık hem internetten indirir, hem lokale kaydeder, hem de kullanıcının hakkını Firestore'a işler.
    suspend fun downloadAndSaveStory(storyDocId: String) {
        try {
            val document = db.collection("stories").document(storyDocId).get().await()
            document.data?.let { data ->
                localDataSource.saveStory(storyDocId, data)
                addDownloadedStoryIdToFirestore(storyDocId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Bu fonksiyon artık SADECE telefon hafızasından hikayeyi siler, kullanıcının hakkını silmez.
    suspend fun deleteStory(storyId: String) {
        localDataSource.deleteStory(storyId)
    }

    // Bu fonksiyon Firestore'daki tüm hikayelerin genel listesini çeker.
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

    // Bu fonksiyon, telefon hafızasındaki tek bir hikayenin içeriğini okur.
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