package com.ntikhoa.ocrreceipt.business.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.VoucherResponse

data class Voucher(
    val ID: Int,
    val Name: String,
    val Description: String,
    val GiftName: String,
) {
    companion object {
        fun fromResponse(res: VoucherResponse): Voucher {
            return Voucher(
                ID = res.ID,
                Name = res.Name,
                Description = res.Description,
                GiftName = res.GiftName,
            )
        }

        fun fromResponses(voucherResponses: List<VoucherResponse>): List<Voucher> {
            return voucherResponses.map { fromResponse(it) }
        }
    }
}