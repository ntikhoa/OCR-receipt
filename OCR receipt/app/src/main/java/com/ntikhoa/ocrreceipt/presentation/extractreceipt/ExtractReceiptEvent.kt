package com.ntikhoa.ocrreceipt.presentation.extractreceipt

import android.net.Uri

sealed class ExtractReceiptEvent {
    data class ExtractReceiptImage(val imageUri: Uri) : ExtractReceiptEvent()
}