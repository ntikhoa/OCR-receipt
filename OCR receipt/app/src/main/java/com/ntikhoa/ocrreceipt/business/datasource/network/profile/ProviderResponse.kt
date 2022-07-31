package com.ntikhoa.ocrreceipt.business.datasource.network.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProviderResponse(
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Name")
    val name: String,
)