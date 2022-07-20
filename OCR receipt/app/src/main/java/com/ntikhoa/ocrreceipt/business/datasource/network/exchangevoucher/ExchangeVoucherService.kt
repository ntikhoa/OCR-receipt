package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ExchangeVoucherService {
    @POST("api/v1/exchange_voucher/view")
    @FormUrlEncoded
    suspend fun viewExchangeVoucher(
        @Header("Authorization") token: String,
        @Field("products") products: List<String>,
        @Field("prices") prices: List<Int>
    ): GenericResponse<ExchangeVoucherResponse>
}