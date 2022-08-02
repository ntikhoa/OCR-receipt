package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReceiptImageResponse(
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Url")
    val url: String
)