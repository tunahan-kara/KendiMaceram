package com.kendimaceram.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val appCtx: Context
) : ViewModel() {

    suspend fun requestAccountDeletion(note: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: return@withContext Result.failure(IllegalStateException("No user"))
            val uid = user.uid
            val nowMs = System.currentTimeMillis()
            val expiresMs = nowMs + 30L * 24 * 60 * 60 * 1000 // 30 gün

            val data = hashMapOf(
                "uid" to uid,
                "email" to (user.email ?: ""),
                "displayName" to (user.displayName ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "requestedAt" to FieldValue.serverTimestamp(),
                "expiresAt" to Timestamp(Date(expiresMs)),
                "status" to "pending",
                "note" to (note ?: "")
            )

            // delete/{uid}
            firestore.collection("delete").document(uid).set(data, SetOptions.merge()).await()

            // users/{uid} içine bayrak (opsiyonel)
            firestore.collection("users").document(uid).set(
                mapOf(
                    "pendingDeletion" to true,
                    "deletionExpiresAt" to Timestamp(Date(expiresMs))
                ),
                SetOptions.merge()
            ).await()

            Result.success(Unit)
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
