package com.ntikhoa.ocrreceipt.business.domain.model

import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactionlist.ReceiptResponse

data class Transaction(
    val id: Int,
    val status: String,
    val voucherName: String
) {
    companion object {
        fun fromResponse(response: ReceiptResponse): Transaction {
            return Transaction(
                id = response.id,
                status = response.statusResponse.description,
                voucherName = response.voucher,
            )
        }

        fun fromListResponse(response: List<ReceiptResponse>): List<Transaction> {
            return response.map { fromResponse(it) }
        }

    }
}