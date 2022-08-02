package com.ntikhoa.ocrreceipt.business.domain.model

import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail.ReceiptDetailResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail.VoucherTransactionResponse


data class TransactionDetail(
    val customer: Customer,
    val id: Int,
    val imageUrl: List<String>,
    val status: String,
    val transactionID: String,
    val voucher: Voucher,
) {
    companion object {
        fun fromResponse(response: ReceiptDetailResponse): TransactionDetail {
            return TransactionDetail(
                id = response.id,
                customer = Customer.fromResponse(response.customerResponse),
                imageUrl = response.receiptImageResponses.map { it.url },
                transactionID = response.transactionID,
                status = response.status.description,
                voucher = Voucher.fromVoucherTransactionResponse(response.voucherTransactionResponses[0])
            )
        }
    }
}