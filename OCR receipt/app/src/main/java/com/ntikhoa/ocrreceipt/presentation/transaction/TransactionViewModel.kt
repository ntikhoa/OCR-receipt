package com.ntikhoa.ocrreceipt.presentation.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.transaction.GetTransactionsUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel
@Inject
constructor(
    private val getTransactionsUC: GetTransactionsUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<TransactionEvent> {

    private val _state = MutableStateFlow(TransactionState())
    val state get() = _state.asStateFlow()

    private var getTransactionJob: Job? = null

    override fun onTriggerEvent(event: TransactionEvent) {
        viewModelScope.launch {
            when (event) {
                is TransactionEvent.GetListTransaction -> {
                    resetPage()
                    sessionManager.token?.let {
                        getTransactionList(it, _state.value.page, _state.value.perPage)
                    } ?: run {
                        _state.value = TransactionState(message = "Invalid Token")
                    }
                }
                is TransactionEvent.GetListTransactionNextPage -> {
                    sessionManager.token?.let {
                        _state.value.page += 1
                        getTransactionList(it, _state.value.page, _state.value.perPage)
                    } ?: run {
                        _state.value = TransactionState(message = "Invalid Token")
                    }
                }
            }
        }
    }

    private fun resetPage() {
        _state.value.page = 1
        _state.value.isExhausted = false
    }


    private suspend fun getTransactionList(token: String, page: Int, perPage: Int) {
        getTransactionJob?.cancel()
        _state.value = _state.value.copy(loading = true)
        getTransactionJob = getTransactionsUC(token, page, perPage).onEach { dataState ->

            val copiedState = _state.value.copy()

            copiedState.loading = dataState.isLoading

            dataState.data?.let {
                if (page > 1) {
                    val list = copiedState.transactions?.toMutableList()
                    list?.addAll(it)
                    copiedState.transactions = list
                } else {
                    copiedState.transactions = it
                }
            }

            dataState.message?.let {
                if (it.contains("exhausted")) {
                    copiedState.isExhausted = true
                }
                copiedState.message = it
            }

            _state.value = copiedState

        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun onCancelJob() {
        getTransactionJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        onCancelJob()
    }
}