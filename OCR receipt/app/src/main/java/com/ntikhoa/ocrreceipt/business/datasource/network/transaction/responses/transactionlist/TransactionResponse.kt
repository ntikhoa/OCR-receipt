package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactionlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.datasource.network.PagingResponse

data class TransactionResponse(
    @Expose
    @SerializedName("metadata")
    val metadata: PagingResponse,
    @Expose
    @SerializedName("receipts")
    val receipts: List<ReceiptResponse>,
)