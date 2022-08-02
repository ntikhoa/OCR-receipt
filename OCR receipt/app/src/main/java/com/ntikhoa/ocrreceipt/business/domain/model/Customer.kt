package com.ntikhoa.ocrreceipt.business.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail.CustomerResponse

data class Customer(
    val id: Int,
    val name: String,
    val phone: String,
) {
    companion object {
        fun fromResponse(response: CustomerResponse): Customer {
            return Customer(
                id = response.id,
                name = response.name,
                phone = response.phone,
            )
        }
    }
}