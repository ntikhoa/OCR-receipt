package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.domain.model.CustomerInfo
import com.ntikhoa.ocrreceipt.business.domain.model.ProductSearch
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.business.getImageUri
import com.ntikhoa.ocrreceipt.business.usecase.exchangevoucher.ExchangeVoucherUC
import com.ntikhoa.ocrreceipt.business.usecase.exchangevoucher.ViewExchangeVoucherUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessExtractedReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import com.ntikhoa.ocrreceipt.business.usecase.fulltextsearch.BuildDocTermMatrixUC
import com.ntikhoa.ocrreceipt.business.usecase.fulltextsearch.SearchProductsUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher.ViewExchangeVoucherState
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt.ScanReceiptState
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.exchangedone.ExchangeVoucherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ExchangeVoucherViewModel
@Inject
constructor(
    private val processImageUC: ProcessImageUC,
    private val ocr: OCRUseCase,
    private val extractReceiptUC: ExtractReceiptUC,
    private val processExtractedReceiptUC: ProcessExtractedReceiptUC,
    private val viewExchangeVoucherUC: ViewExchangeVoucherUC,
    private val exchangeVoucherUC: ExchangeVoucherUC,
    private val buildDocTermMatrixUC: BuildDocTermMatrixUC,
    private val searchProductsUC: SearchProductsUC,
    private val sessionManager: SessionManager,
) : ViewModel(), OnTriggerEvent<ExchangeVoucherEvent> {

    var customerInfo = CustomerInfo()
    var croppedImage: Bitmap? = null
    var image: Bitmap? = null

    var evidenceImage: Bitmap? = null

    private val products = mutableListOf<String>()
    private val prices = mutableListOf<Int>()
    var voucher: Voucher? = null

    private var docTermMatrix = mapOf<String, Map<String, Float>>()

    private val _state = MutableStateFlow(ScanReceiptState())
    val state get() = _state.asStateFlow()

    private val _viewExchangeState = MutableStateFlow(ViewExchangeVoucherState())
    val viewExchangeState get() = _viewExchangeState.asStateFlow()

    private val _exchangeState = MutableStateFlow(ExchangeVoucherState())
    val exchangeState get() = _exchangeState.asStateFlow()

    private var processImageJob: Job? = null
    private var ocrJob: Job? = null
    private var extractReceiptJob: Job? = null
    private var processExtractReceiptJob: Job? = null
    private var viewExchangeVoucherJob: Job? = null
    private var exchangeVoucherJob: Job? = null


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
                is ExchangeVoucherEvent.ExchangeVoucher -> {
                    sessionManager.token?.let {
                        exchangeVoucher(it, event.outputDir)
                    } ?: run {
                        _viewExchangeState.value =
                            _viewExchangeState.value.copy(message = "Invalid Token")
                    }
                }
                is ExchangeVoucherEvent.BuildDocTermMatrix -> {
                    sessionManager.token?.let {
                        buildDocTermMatrix(it)
                    } ?: run {
                        _viewExchangeState.value =
                            _viewExchangeState.value.copy(message = "Invalid Token")
                    }
                }
            }
        }
    }

    private suspend fun buildDocTermMatrix(token: String) {
        buildDocTermMatrixUC(token).onEach { dataState ->

            dataState.data?.let {
                docTermMatrix = it
            }

        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private suspend fun exchangeVoucher(token: String, outputDir: File) {
        val imagesUri = listOf(image!!, evidenceImage)
        val imagesRequestBody = imagesUri.map {
            val file = getImageUri(outputDir, it!!)
            val requestBody = RequestBody.create(
                MediaType.parse("image/*"),
                file
            )
            MultipartBody.Part.createFormData(
                "files",
                file.name,
                requestBody
            )
        }

        val productsRequestBody =
            products.map { MultipartBody.Part.createFormData("products", it) }
        val pricesRequestBody =
            prices.map { MultipartBody.Part.createFormData("prices", it.toString()) }
        val voucherIDRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), voucher!!.id.toString())
        val customerNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerInfo.customerName)
        val customerPhoneRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerInfo.customerPhone)
        val transactionIDRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerInfo.transactionID)

        exchangeVoucherJob?.cancel()
        exchangeVoucherJob = exchangeVoucherUC(
            token,
            imagesRequestBody,
            productsRequestBody,
            pricesRequestBody,
            voucherIDRequestBody,
            customerNameRequestBody,
            customerPhoneRequestBody,
            transactionIDRequestBody
        ).onEach { dataState ->
            val copiedState = _exchangeState.value.copy()

            copiedState.isLoading = dataState.isLoading

            dataState.data?.let {
                copiedState.data = it
            }

            dataState.message?.let { msg ->
                copiedState.message = msg
            }

            _exchangeState.value = copiedState
        }.flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun getCurrentProducts(): MutableList<ProductSearch>? {
        return state.value.productsSearch
    }

    fun getCurrentPrices(): MutableList<String>? {
        return state.value.prices
    }

    fun submitReceipt() {
        if (getCurrentProducts().isNullOrEmpty() && getCurrentPrices().isNullOrEmpty()) {
            throw Exception("Products and/or Prices is empty")
        }
        if (getCurrentProducts()?.size != getCurrentPrices()?.size) {
            throw Exception("Products and Prices does not match")
        }
        getCurrentProducts()?.let { products.addAll(it.map { productSearch -> productSearch.productName }) }
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
        viewExchangeVoucherJob =
            viewExchangeVoucherUC(token, products, prices).onEach { dataState ->
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
                    copiedState.prices = it.prices
                    searchProducts(it.products)
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

    private suspend fun searchProducts(products: List<String>) {
        searchProductsUC(products, docTermMatrix)
            .onEach { dataState ->
                val copiedState = _state.value.copy()

                dataState.data?.let {
                    copiedState.isLoading = false
                    copiedState.productsSearch = it.toMutableList()
                }

                dataState.message?.let {
                    copiedState.isLoading = false
                    copiedState.message = it
                }

                _state.value = copiedState

            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    fun onClearedEditReceipt() {
        _state.value = ScanReceiptState()
    }

    fun onCleardDoneExchange() {
        _exchangeState.value = ExchangeVoucherState()
    }

    fun cancelJobs() {
        processImageJob?.cancel()
        ocrJob?.cancel()
        extractReceiptJob?.cancel()
        processExtractReceiptJob?.cancel()
        viewExchangeVoucherJob?.cancel()
        exchangeVoucherJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}