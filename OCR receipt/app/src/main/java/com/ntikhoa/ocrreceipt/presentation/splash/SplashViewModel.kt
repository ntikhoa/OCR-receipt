package com.ntikhoa.ocrreceipt.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.auth.AutoLoginUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    private val autoLoginUC: AutoLoginUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<SplashEvent> {

    private val _state = MutableStateFlow<SplashState>(SplashState.None)
    val state get() = _state.asStateFlow()

    private var autoLoginJob: Job? = null

    override fun onTriggerEvent(event: SplashEvent) {
        viewModelScope.launch {
            when (event) {
                is SplashEvent.AutoLogin -> {
                    autoLogin()
                }
            }
        }
    }

    private suspend fun autoLogin() {
        autoLoginJob?.cancel()
        autoLoginJob = autoLoginUC()
            .onEach { dataState ->
                dataState.data?.let {
                    sessionManager.login(it.token, it.name)
                    _state.value = SplashState.AutoLoginSuccess
                }

                dataState.message?.let {
                    _state.value = SplashState.AutoLoginFail
                }
            }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun cancelJobs() {
        autoLoginJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}