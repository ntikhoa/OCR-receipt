package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import com.ntikhoa.ocrreceipt.presentation.chooseimage.ChooseImageEvent
import com.ntikhoa.ocrreceipt.presentation.chooseimage.ChooseImageState
import com.ntikhoa.ocrreceipt.presentation.extractreceipt.ExtractReceiptState
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
) : ViewModel(), OnTriggerEvent<ChooseImageEvent> {

    var bitmap: Bitmap? = null

    private val _state = MutableStateFlow(ChooseImageState())
    val state get() = _state.asStateFlow()

    private val _anotherState = MutableStateFlow(ExtractReceiptState())
    val anotherState get() = _anotherState.asStateFlow()

    private val _scanState = MutableStateFlow(ExtractReceiptState())
    val scanState get() = _anotherState.asStateFlow()

    private var processImageJob: Job? = null
    private var ocrJob: Job? = null
    private var extractReceiptJob: Job? = null

    override fun onTriggerEvent(event: ChooseImageEvent) {
        viewModelScope.launch {
            when (event) {
                is ChooseImageEvent.ProcessBitmapImageEvent -> {
                    processImage(event.bitmap)
                }
                is ChooseImageEvent.ProcessMatImageEvent -> {
                    processImage(event.mat)
                }


                is ChooseImageEvent.ExtractReceiptImage -> {
                    extractReceipt(event.bitmap)
                }
                is ChooseImageEvent.ScanReceipt -> {
                    scanReceipt(event.bitmap)
                }
            }
        }
    }


    private suspend fun processImage(mat: Mat) {
        processImageJob?.cancel()
        processImageJob = processImageUC(mat).onEach { dataState ->
            val copiedState = _state.value.copy()

            copiedState.isLoading = dataState.isLoading
            dataState.data?.let { data ->
                copiedState.bitmap = data
            }
            dataState.message?.let { msg ->
                copiedState.message = msg
            }

            _state.value = copiedState
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun processImage(bitmap: Bitmap) {
        processImageJob?.cancel()
        processImageJob = processImageUC(bitmap).onEach { dataState ->
            val copiedState = _state.value.copy()

            copiedState.isLoading = dataState.isLoading
            dataState.data?.let { data ->
                copiedState.bitmap = data
            }
            dataState.message?.let { msg ->
                copiedState.message = msg
            }

            _state.value = copiedState
        }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun extractReceipt(imageUri: Uri) {
        ocrJob?.cancel()
        ocr(imageUri)
        ocrJob = ocr(imageUri)
            .onEach { dataState ->
                val copiedState = _anotherState.value.copy()
                if (dataState.isLoading) {
                    copiedState.isLoading = dataState.isLoading
                }

                dataState.data?.let {
                    extractReceiptText(it)
                }
                _anotherState.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    //Scan Receipt Here

    private suspend fun scanReceipt(bitmap: Bitmap) {
        processImageJob?.cancel()
        processImageJob = processImageUC(bitmap).onEach { dataState ->
            val copiedState = _state.value.copy()

            copiedState.isLoading = dataState.isLoading
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
                val copiedState = _anotherState.value.copy()
                if (dataState.isLoading) {
                    copiedState.isLoading = dataState.isLoading
                }

                dataState.data?.let {
                    extractReceiptText(it)
                }
                _anotherState.value = copiedState
            }.flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private suspend fun extractReceiptText(visionText: Text) {
        extractReceiptJob?.cancel()
        extractReceiptJob = extractReceiptUC(visionText)
            .onEach { dataState ->
                val copiedState = _anotherState.value.copy()

                copiedState.isLoading = dataState.isLoading
                dataState.data?.let {
                    copiedState.text = it
                }

                _anotherState.value = copiedState
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