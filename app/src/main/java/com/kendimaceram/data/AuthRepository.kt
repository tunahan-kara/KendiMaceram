package com.kendimaceram.app.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val firebaseAuth: FirebaseAuth = Firebase.auth

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    // Dönen tip artık AuthResource
    suspend fun createUser(email: String, password: String): AuthResource {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            AuthResource.Success
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthWeakPasswordException -> "Şifre çok zayıf. Lütfen en az 6 karakter girin."
                is FirebaseAuthUserCollisionException -> "Bu e-posta adresi zaten kullanılıyor."
                else -> "Bir hata oluştu: ${e.message}"
            }
            AuthResource.Failure(errorMessage)
        }
    }

    // Dönen tip artık AuthResource
    suspend fun signIn(email: String, password: String): AuthResource {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResource.Success
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> "E-posta veya şifre hatalı."
                else -> "Bir hata oluştu: ${e.message}"
            }
            AuthResource.Failure(errorMessage)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}