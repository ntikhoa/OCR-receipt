package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap
import java.io.File

sealed class ExchangeVoucherEvent {
    data class ScanReceipt(val bitmap: Bitmap) : ExchangeVoucherEvent()
    object ViewExchangeVoucher : ExchangeVoucherEvent()
    data class ExchangeVoucher(val outputDir: File) : ExchangeVoucherEvent()
}