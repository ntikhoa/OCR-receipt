package com.ntikhoa.ocrreceipt.presentation.transaction.transactiondetail

import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.transaction.GetTransactionDetailUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel
@Inject
constructor(
    private val getTransactionDetailUC: GetTransactionDetailUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<TransactionDetailEvent> {

    private val _state = MutableStateFlow(TransactionDetailState())
    val state get() = _state.asStateFlow()

    private var getTransactionDetailJob: Job? = null

    override fun onTriggerEvent(event: TransactionDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is TransactionDetailEvent.GetTransactionDetail -> {
                    sessionManager.token?.let {
                        getTransactionDetail(it, event.id)
                    } ?: run {
                        _state.value = _state.value.copy(message = "Invalid token")
                    }
                }
            }
        }
    }

    private suspend fun getTransactionDetail(token: String, id: Int) {
        getTransactionDetailJob?.cancel()
        getTransactionDetailJob = getTransactionDetailUC(token, id).onEach { dataState ->

            val copiedState = _state.value.copy()

            copiedState.isLoading = dataState.isLoading

            dataState.data?.let {
                copiedState.transactionDetail = it
            }

            dataState.message?.let {
                copiedState.message = it
            }

            _state.value = copiedState

        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onCancelJob() {
        getTransactionDetailJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        onCancelJob()
    }
}