package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CustomerResponse(
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Name")
    val name: String,
    @Expose
    @SerializedName("Phone")
    val phone: String,
)