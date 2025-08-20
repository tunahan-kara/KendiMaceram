package com.kendimaceram.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kendimaceram.app.data.AuthRepository
import com.kendimaceram.app.data.AuthResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // Flow artık AuthResource taşıyor
    private val _eventFlow = MutableSharedFlow<AuthResource>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onRegisterClick() {
        viewModelScope.launch {
            isLoading = true
            val result = repository.createUser(email, password)
            _eventFlow.emit(result)
            isLoading = false
        }
    }
}