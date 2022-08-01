package com.ntikhoa.ocrreceipt.business.datasource.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PagingResponse(
    @SerializedName("page")
    @Expose
    val page: Int,
    @SerializedName("per_page")
    @Expose
    val perPage: Int,
    @SerializedName("total_pages")
    @Expose
    val totalPages: Int,
    @SerializedName("total_records")
    @Expose
    val totalRecords: Int,
)