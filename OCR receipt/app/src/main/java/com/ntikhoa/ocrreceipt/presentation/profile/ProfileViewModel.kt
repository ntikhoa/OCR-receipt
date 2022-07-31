package com.ntikhoa.ocrreceipt.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.profile.GetProfileAccountUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val getProfileAccountUC: GetProfileAccountUC,
) : ViewModel(), OnTriggerEvent<ProfileEvent> {

    private val _state = MutableStateFlow(ProfileState())
    val state get() = _state.asStateFlow()

    private var getProfileJob: Job? = null

    fun getAvatarURL(): String {
        val query = sessionManager.name?.let {
            val words = it.split(" ")
            var queryName = "John Doe"
            if (words.size > 2) {
                val name = words.last()
                val middleName = words[words.lastIndex - 1]
                queryName = "$middleName+$name"
            } else if (words.size == 1) {
                queryName = words[0]
            }
            queryName
        } ?: "John Doe"
        return "https://ui-avatars.com/api/?name=$query"
    }

    fun logout() {
        sessionManager.logout()
    }

    override fun onTriggerEvent(event: ProfileEvent) {
        viewModelScope.launch {
            when (event) {
                is ProfileEvent.GetProfileAccount -> {
                    sessionManager.token?.let {
                        getProfileAccount(it)
                    } ?: run {
                        _state.value = _state.value.copy(message = "Invalid Token")
                    }
                }
            }
        }
    }

    private suspend fun getProfileAccount(token: String) {
        getProfileJob?.cancel()
        getProfileJob = getProfileAccountUC(token).onEach { dataState ->
            val copiedState = _state.value.copy()

            copiedState.isLoading = dataState.isLoading

            dataState.data?.let {
                copiedState.profile = it
            }

            dataState.message?.let {
                copiedState.message = it
            }

            _state.value = copiedState
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onCancelJob() {
        getProfileJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        onCancelJob()
    }
}