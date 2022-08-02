package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VoucherTransactionResponse(
    @Expose
    @SerializedName("Description")
    val description: String,
    @Expose
    @SerializedName("Gift")
    val giftResponse: GiftResponse,
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Name")
    val name: String,
)