package com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactiondetail

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.transactionlist.StatusResponse

data class ReceiptDetailResponse(
    @Expose
    @SerializedName("Customer")
    val customerResponse: CustomerResponse,
    @Expose
    @SerializedName("ID")
    val id: Int,
    @Expose
    @SerializedName("ReceiptImages")
    val receiptImageResponses: List<ReceiptImageResponse>,
    @Expose
    @SerializedName("Status")
    val status: StatusResponse,
    @Expose
    @SerializedName("TransactionID")
    val transactionID: String,
    @Expose
    @SerializedName("Voucher")
    val voucherTransactionResponses: List<VoucherTransactionResponse>
)