package com.ntikhoa.ocrreceipt.business.datasource.network.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetProfileResponse(
    @Expose
    @SerializedName("profile")
    val profileResponse: ProfileResponse,
)