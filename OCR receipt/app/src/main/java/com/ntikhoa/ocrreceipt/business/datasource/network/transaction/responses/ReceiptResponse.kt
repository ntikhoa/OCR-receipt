package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReceiptResponse(
    @Expose
    @SerializedName("Account")
    val account: String,
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Status")
    val statusResponse: StatusResponse,
    @Expose
    @SerializedName("TransactionID")
    val transactionID: String,
    @Expose
    @SerializedName("Voucher")
    val voucher: String
)