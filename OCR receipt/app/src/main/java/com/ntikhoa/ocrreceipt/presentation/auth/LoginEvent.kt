package com.ntikhoa.ocrreceipt.presentation.auth

sealed class LoginEvent {
    data class Login(val username: String, val password: String) : LoginEvent()
}