package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GiftResponse(
    @Expose
    @SerializedName("GiftName")
    val giftName: String,
    @Expose
    @SerializedName("ID")
    val id: Int,
)