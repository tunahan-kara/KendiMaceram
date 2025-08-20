// data/AuthResource.kt
package com.kendimaceram.app.data

sealed class AuthResource {
    data object Success : AuthResource()
    data class Failure(val message: String) : AuthResource()
}