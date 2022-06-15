package com.ntikhoa.ocrreceipt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.LoginUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val loginUC: LoginUC
) : ViewModel(), OnTriggerEvent<LoginEvent> {

    private val _state = MutableStateFlow(LoginState())
    val state get() = _state.asStateFlow()

    override fun onTriggerEvent(event: LoginEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginEvent.Login -> {
                    login(event.username, event.password)
                }
            }
        }
    }

    private suspend fun login(username: String, password: String) {
        loginUC(username, password)
            .onEach { dataState ->

                println(dataState.isLoading)

                dataState.data?.let {
                    println(it)
                }
                dataState.message?.let {
                    println(it)
                }

            }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}