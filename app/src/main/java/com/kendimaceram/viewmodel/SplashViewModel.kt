package com.kendimaceram.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kendimaceram.app.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    // Bu ViewModel oluşturulduğunda, AuthRepository'ye o an bir kullanıcı var mı diye sorar.
    val isUserAuthenticated: Boolean = repository.currentUser != null
}