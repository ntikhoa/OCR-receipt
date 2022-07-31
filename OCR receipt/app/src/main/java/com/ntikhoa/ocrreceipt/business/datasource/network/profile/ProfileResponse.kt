package com.ntikhoa.ocrreceipt.business.datasource.network.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("Name")
    val name: String,
    @Expose
    @SerializedName("Provider")
    val providerResponse: ProviderResponse,
    @Expose
    @SerializedName("Username")
    val username: String
)