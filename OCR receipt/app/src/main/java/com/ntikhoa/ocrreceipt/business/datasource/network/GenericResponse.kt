package com.ntikhoa.ocrreceipt.business.datasource.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GenericResponse<T>(
    @SerializedName("data")
    @Expose
    val data: T?,

    @SerializedName("status")
    @Expose
    val status: Int,

    @SerializedName("error")
    @Expose
    val error: String?,

    @SerializedName("message")
    @Expose
    val message: String
)