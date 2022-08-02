package com.ntikhoa.ocrreceipt.business.domain.model

import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response.VoucherResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail.VoucherTransactionResponse

data class Voucher(
    val id: Int,
    val name: String,
    val description: String,
    val giftName: String,
) {
    companion object {
        fun fromResponse(res: VoucherResponse): Voucher {
            return Voucher(
                id = res.ID,
                name = res.Name,
                description = res.Description,
                giftName = res.GiftName,
            )
        }

        fun fromResponses(voucherResponses: List<VoucherResponse>): List<Voucher> {
            return voucherResponses.map { fromResponse(it) }
        }

        fun fromVoucherTransactionResponse(voucherTransactionResponse: VoucherTransactionResponse): Voucher {
            return Voucher(
                id = voucherTransactionResponse.id,
                name = voucherTransactionResponse.name,
                description = voucherTransactionResponse.description,
                giftName = voucherTransactionResponse.giftResponse.giftName
            )
        }
    }
}