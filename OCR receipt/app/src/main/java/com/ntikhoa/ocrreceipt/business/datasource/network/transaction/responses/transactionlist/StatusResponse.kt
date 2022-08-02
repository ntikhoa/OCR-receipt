package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactionlist

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @Expose
    @SerializedName("Description")
    val description: String,
    @Expose
    @SerializedName("ID")
    val id: Int,
)