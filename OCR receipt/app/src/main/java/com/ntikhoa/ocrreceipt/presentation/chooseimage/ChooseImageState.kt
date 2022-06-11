package com.ntikhoa.ocrreceipt.presentation.chooseimage

import android.graphics.Bitmap

data class ChooseImageState(
    var isLoading: Boolean = false,
    var bitmap: Bitmap? = null,
    var message: String? = null
)