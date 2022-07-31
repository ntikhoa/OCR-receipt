package com.ntikhoa.ocrreceipt.presentation.home

import androidx.lifecycle.ViewModel
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val sessionManager: SessionManager
) : ViewModel() {
    fun getFirstName(): String {
        return "Xin Chào, ${sessionManager.name?.split(" ")?.last() ?: " Thế Giới"}!"
    }

    fun isAuth(): Boolean {
        return sessionManager.token != null
    }
}