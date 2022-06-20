package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.graphics.Bitmap
import android.net.Uri
import com.ntikhoa.ocrreceipt.presentation.extractreceipt.ExtractReceiptEvent
import org.opencv.core.Mat

sealed class ChooseImageEvent {
    data class ProcessBitmapImageEvent(val bitmap: Bitmap) : ChooseImageEvent()
    data class ProcessMatImageEvent(val mat: Mat) : ChooseImageEvent()

    data class ExtractReceiptImage(val bitmap: Bitmap) : ChooseImageEvent()
    data class ScanReceipt(val bitmap: Bitmap) : ChooseImageEvent()

}