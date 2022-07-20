package com.ntikhoa.ocrreceipt.presentation

object SessionManager {
    var name: String? = null
        private set

    var token: String? = null
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