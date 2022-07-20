package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import android.graphics.Bitmap

sealed class ExchangeVoucherEvent {
    data class ScanReceipt(val bitmap: Bitmap) : ExchangeVoucherEvent()
    object ViewExchangeVoucher : ExchangeVoucherEvent()
}