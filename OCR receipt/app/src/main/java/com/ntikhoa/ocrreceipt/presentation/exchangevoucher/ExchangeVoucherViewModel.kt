package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.business.usecase.exchangevoucher.ExchangeVoucherUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessExtractedReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher.ExchangeVoucherState
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt.ScanReceiptState
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
    private val exchangeVoucherUC: ExchangeVoucherUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<ExchangeVoucherEvent> {

    var croppedImage: Bitmap? = null
    var image: Bitmap? = null

    private val products = mutableListOf<String>()
    private val prices = mutableListOf<Int>()
    var voucher: Voucher? = null
        private set

    private val _state = MutableStateFlow(ScanReceiptState())
    val state get() = _state.asStateFlow()

    private val _viewExchangeState = MutableStateFlow(ExchangeVoucherState())
    val viewExchangeState get() = _viewExchangeState.asStateFlow()

    private var processImageJob: Job? = null
    private var ocrJob: Job? = null
    private var extractReceiptJob: Job? = null
    private var processExtractReceiptJob: Job? = null
    private var viewExchangeVoucherJob: Job? = null


    override fun onTriggerEvent(event: ExchangeVoucherEvent) {
        viewModelScope.launch {
            when (event) {
                is ExchangeVoucherEvent.ScanReceipt -> {
                    scanReceipt(event.bitmap)
                }
                is ExchangeVoucherEvent.ViewExchangeVoucher -> {
                    sessionManager.token?.let {
                        viewExchangeVoucher(it)
                    } ?: run {
                        _viewExchangeState.value =
                            _viewExchangeState.value.copy(message = "Invalid Token")
                    }
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
        getCurrentPrices()?.let {
            prices.addAll(
                it.map { it.replace(",", "").toInt() }
            )
        }

    }

    fun submitVoucher(position: Int): Voucher? {
        voucher = _viewExchangeState.value.voucher?.let {
            it[position]
        }
        return voucher
    }

    private suspend fun viewExchangeVoucher(token: String) {
        viewExchangeVoucherJob?.cancel()
        viewExchangeVoucherJob = exchangeVoucherUC(token, products, prices).onEach { dataState ->
            val copiedState = _viewExchangeState.value.copy()

            copiedState.isLoading = dataState.isLoading

            dataState.data?.let { data ->
                copiedState.voucher = data
            }

            dataState.message?.let { msg ->
                copiedState.message = msg
            }

            _viewExchangeState.value = copiedState
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
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
        viewExchangeVoucherJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}