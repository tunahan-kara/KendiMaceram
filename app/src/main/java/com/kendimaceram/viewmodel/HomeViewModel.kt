// viewmodel/HomeViewModel.kt
package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kendimaceram.app.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    fun signOut() {
        repository.signOut()
    }
}