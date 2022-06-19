package com.ntikhoa.ocrreceipt.presentation.extractreceipt

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.OCRUseCase
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtractReceiptViewModel
@Inject
constructor(
    private val ocr: OCRUseCase,
    private val extractReceiptUC: ExtractReceiptUC,
) : ViewModel(), OnTriggerEvent<ExtractReceiptEvent> {

    private val _state = MutableStateFlow(ExtractReceiptState())
    val state get() = _state.asStateFlow()

    private var extractReceiptJob: Job? = null
    private var ocrJob: Job? = null

    override fun onTriggerEvent(event: ExtractReceiptEvent) {
        viewModelScope.launch {
            when (event) {
                is ExtractReceiptEvent.ExtractReceiptImage -> {
                    extractReceipt(event.imageUri)
                }
            }
        }
    }

    private suspend fun extractReceipt(imageUri: Uri) {
        ocrJob?.cancel()
        ocrJob = ocr(imageUri)
            .onEach { dataState ->
                val copiedState = _state.value.copy()
                if (dataState.isLoading) {
                    copiedState.isLoading = dataState.isLoading
                }

                dataState.data?.let {
                    extractReceiptText(it)
                }
                _state.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun extractReceiptText(visionText: Text) {
        extractReceiptJob?.cancel()
        extractReceiptJob = extractReceiptUC(visionText)
            .onEach { dataState ->
                val copiedState = _state.value.copy()

                copiedState.isLoading = dataState.isLoading
                dataState.data?.let {
                    copiedState.text = it
                }

                _state.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun cancelJobs() {
        extractReceiptJob?.cancel()
        ocrJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}