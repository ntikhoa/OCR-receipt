package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import javax.inject.Inject

@HiltViewModel
class ExchangeVoucherViewModel
@Inject
constructor(
    private val processImageUC: ProcessImageUC,
    private val ocr: OCRUseCase,
    private val extractReceiptUC: ExtractReceiptUC,
) : ViewModel(), OnTriggerEvent<ExchangeVoucherEvent> {

    var croppedImage: Bitmap? = null
    var image: Bitmap? = null

    private val _state = MutableStateFlow(ExchangeVoucherState())
    val state get() = _state.asStateFlow()

    private var processImageJob: Job? = null
    private var ocrJob: Job? = null
    private var extractReceiptJob: Job? = null

    override fun onTriggerEvent(event: ExchangeVoucherEvent) {
        viewModelScope.launch {
            when (event) {
                is ExchangeVoucherEvent.ScanReceipt -> {
                    scanReceipt(event.bitmap)
                }
            }
        }
    }

    private suspend fun scanReceipt(bitmap: Bitmap) {
        processImageJob?.cancel()
        processImageJob = processImageUC(bitmap).onEach { dataState ->
            val copiedState = _state.value.copy()

            if (dataState.isLoading) copiedState.isLoading = true

            dataState.data?.let { data ->
                extractReceipt(data)
            }
            dataState.message?.let { msg ->
                copiedState.message = msg
            }

            _state.value = copiedState
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun extractReceipt(bitmap: Bitmap) {
        ocrJob?.cancel()
        ocr(bitmap)
        ocrJob = ocr(bitmap)
            .onEach { dataState ->
                val copiedState = _state.value.copy()

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
                    copiedState.receipt = it
                }

                _state.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun cancelJobs() {
        processImageJob?.cancel()
        ocrJob?.cancel()
        extractReceiptJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}