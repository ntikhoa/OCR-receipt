package com.ntikhoa.ocrreceipt.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.usecase.AutoLoginUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
        autoLoginUC()
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
}