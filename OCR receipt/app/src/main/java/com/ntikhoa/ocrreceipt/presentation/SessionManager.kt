package com.ntikhoa.ocrreceipt.presentation

data class SessionManager(
    private var token: String? = null,
) {
    var name: String? = null
        private set


    fun login(token: String, name: String) {
        this.token = token
        this.name = name
    }

    fun logout() {
        this.token = null
        this.name = null
    }
}