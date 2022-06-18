package com.ntikhoa.ocrreceipt.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.usecase.LoginUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val loginUC: LoginUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<LoginEvent> {

    private val _state = MutableStateFlow<LoginState>(LoginState.None)
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
                if (dataState.isLoading) {
                    _state.value = LoginState.Loading
                }

                dataState.data?.let {
                    sessionManager.login(it.token, it.name)
                    _state.value = LoginState.LoginSuccess
                }

                dataState.message?.let {
                    if (it.contains("invalid username or password")) {
                        _state.value = LoginState.LoginFailed("invalid username or password")
                    } else if (it == Constants.UNKNOWN_ERROR) {
                        _state.value = LoginState.LoginFailed(Constants.UNKNOWN_ERROR)
                    } else {
                        _state.value = LoginState.None
                    }
                }

            }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}