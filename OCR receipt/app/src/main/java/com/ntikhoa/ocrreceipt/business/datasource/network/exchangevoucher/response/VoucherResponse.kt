package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VoucherResponse (
    @SerializedName("ID")
    @Expose
    val ID: Int,
    @SerializedName("Name")
    @Expose
    val Name: String,
    @SerializedName("Description")
    @Expose
    val Description: String,
    @SerializedName("GiftName")
    @Expose
    val GiftName: String,
    @SerializedName("CreatedAt")
    @Expose
    val CreatedAt: String?,
    @SerializedName("DeletedAt")
    @Expose
    val DeletedAt: String?,
    @SerializedName("UpdatedAt")
    @Expose
    val UpdatedAt: String?
)