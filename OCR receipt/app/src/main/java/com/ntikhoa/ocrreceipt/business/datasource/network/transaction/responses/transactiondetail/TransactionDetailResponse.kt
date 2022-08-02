package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TransactionDetailResponse(
    @Expose
    @SerializedName("receipts")
    val receiptDetailResponse: ReceiptDetailResponse
)