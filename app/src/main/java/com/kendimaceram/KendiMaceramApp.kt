package com.kendimaceram.app

import android.app.Application
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KendiMaceramApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase
        val firestore = Firebase.firestore
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        firestore.firestoreSettings = settings
    }
}