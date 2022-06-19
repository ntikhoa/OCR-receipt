package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.usecase.ProcessImageUC
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import javax.inject.Inject

@HiltViewModel
class ChooseImageViewModel
@Inject
constructor(
    private val processImageUC: ProcessImageUC
) : ViewModel(), OnTriggerEvent<ChooseImageEvent> {

    private val _state = MutableStateFlow(ChooseImageState())
    val state get() = _state.asStateFlow()

    private var processImageJob: Job? = null

    override fun onTriggerEvent(event: ChooseImageEvent) {
        viewModelScope.launch {
            when (event) {
                is ChooseImageEvent.ProcessBitmapImageEvent -> {
                    processImage(event.bitmap)
                }
                is ChooseImageEvent.ProcessMatImageEvent -> {
                    processImage(event.mat)
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

    fun cancelJobs() {
        processImageJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}