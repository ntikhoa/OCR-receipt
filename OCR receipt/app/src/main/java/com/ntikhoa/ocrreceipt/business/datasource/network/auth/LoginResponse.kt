package com.ntikhoa.ocrreceipt.business.datasource.network.auth

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.domain.model.Account

class LoginResponse(
    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("token")
    @Expose
    val token: String
) {
    fun toAccount(): Account {
        return Account(
            token = token,
            name = name
        )
    }
}