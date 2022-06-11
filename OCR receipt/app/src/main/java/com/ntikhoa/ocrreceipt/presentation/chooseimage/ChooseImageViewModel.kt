package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.ProcessImageUseCase
import com.ntikhoa.ocrreceipt.presentation.OnTriggerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.opencv.core.Mat
import javax.inject.Inject

@HiltViewModel
class ChooseImageViewModel
@Inject
constructor(
    val processImageUseCase: ProcessImageUseCase
) : ViewModel(), OnTriggerEvent<ChooseImageEvent> {

    private val _state = MutableLiveData(ChooseImageState())
    val state: LiveData<ChooseImageState> get() = _state

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
        _state.value?.let {
            processImageUseCase(mat).onEach { dataState ->
                val copiedState = it.copy()

                copiedState.isLoading = dataState.isLoading
                dataState.data?.let { data ->
                    copiedState.bitmap = data
                }
                dataState.message?.let { msg ->
                    copiedState.message = msg
                }

                _state.postValue(copiedState)
            }.flowOn(Dispatchers.Default)
                .launchIn(viewModelScope)
        }
    }

    private suspend fun processImage(bitmap: Bitmap) {
        _state.value?.let {
            processImageUseCase(bitmap).onEach { dataState ->
                val copiedState = it.copy()

                copiedState.isLoading = dataState.isLoading
                dataState.data?.let { data ->
                    copiedState.bitmap = data
                }
                dataState.message?.let { msg ->
                    copiedState.message = msg
                }

                _state.postValue(copiedState)
            }.flowOn(Dispatchers.Default)
                .launchIn(viewModelScope)
        }
    }
}