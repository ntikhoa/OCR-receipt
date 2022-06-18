package com.ntikhoa.ocrreceipt.presentation.auth

sealed class LoginState(var isLoading: Boolean = false) {
    object Loading : LoginState(isLoading = true)
    object LoginSuccess : LoginState()
    data class LoginFailed(val message: String) : LoginState()
    object None : LoginState()
}