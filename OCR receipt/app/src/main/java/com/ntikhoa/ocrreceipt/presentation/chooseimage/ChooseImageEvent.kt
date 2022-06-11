package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.graphics.Bitmap
import org.opencv.core.Mat

sealed class ChooseImageEvent {
    data class ProcessBitmapImageEvent(val bitmap: Bitmap) : ChooseImageEvent()
    data class ProcessMatImageEvent(val mat: Mat) : ChooseImageEvent()
}