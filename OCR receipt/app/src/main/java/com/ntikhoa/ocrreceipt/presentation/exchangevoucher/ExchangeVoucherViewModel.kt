package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessExtractedReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ExchangeVoucherViewModel
@Inject
constructor(
    private val processImageUC: ProcessImageUC,
    private val ocr: OCRUseCase,
    private val extractReceiptUC: ExtractReceiptUC,
    private val processExtractedReceiptUC: ProcessExtractedReceiptUC,
) : ViewModel(), OnTriggerEvent<ExchangeVoucherEvent> {

    var croppedImage: Bitmap? = null
    var image: Bitmap? = null

    private val products = mutableListOf<String>()
    private val prices = mutableListOf<String>()


    private val _state = MutableStateFlow(ExchangeVoucherState())
    val state get() = _state.asStateFlow()

    private var processImageJob: Job? = null
    private var ocrJob: Job? = null
    private var extractReceiptJob: Job? = null
    private var processExtractReceiptJob: Job? = null


    override fun onTriggerEvent(event: ExchangeVoucherEvent) {
        viewModelScope.launch {
            when (event) {
                is ExchangeVoucherEvent.ScanReceipt -> {
                    scanReceipt(event.bitmap)
                }
            }
        }
    }

    fun getCurrentProducts(): MutableList<String>? {
        return state.value.receipt?.products
    }

    fun getCurrentPrices(): MutableList<String>? {
        return state.value.receipt?.prices
    }

    fun submitReceipt() {
        if (getCurrentProducts().isNullOrEmpty() && getCurrentPrices().isNullOrEmpty()) {
            throw Exception("Products and/or Prices is empty")
        }
        if (getCurrentProducts()?.size != getCurrentPrices()?.size) {
            throw Exception("Products and Prices does not match")
        }
        getCurrentProducts()?.let { products.addAll(it) }
        getCurrentPrices()?.let { prices.addAll(it) }

    }

    private suspend fun scanReceipt(bitmap: Bitmap) {
        processImageJob?.cancel()
        processImageJob = processImageUC(bitmap).onEach { dataState ->
            val copiedState = _state.value.copy()

            if (dataState.isLoading) copiedState.isLoading = true

            dataState.data?.let { data ->
                extractReceipt(data)
            }
            dataState.message?.let {
                copiedState.isLoading = false
                copiedState.message = it
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

                dataState.message?.let {
                    copiedState.isLoading = false
                    copiedState.message = it
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

                dataState.data?.let {
                    processReceiptText(it)
                }

                dataState.message?.let {
                    copiedState.isLoading = false
                    copiedState.message = it
                }

                _state.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun processReceiptText(receipt: Receipt) {
        processExtractReceiptJob?.cancel()
        processExtractReceiptJob = processExtractedReceiptUC(receipt)
            .onEach { dataState ->
                val copiedState = _state.value.copy()

                dataState.data?.let {
                    copiedState.isLoading = false
                    copiedState.receipt = it
                }
                dataState.message?.let {
                    copiedState.isLoading = false
                    copiedState.message = it
                }

                _state.value = copiedState
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun cancelJobs() {
        processImageJob?.cancel()
        ocrJob?.cancel()
        extractReceiptJob?.cancel()
        processExtractReceiptJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}