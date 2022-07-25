package com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response.ExchangeProductsResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response.ExchangeVoucherResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ExchangeVoucherService {
    @POST("api/v1/exchange_voucher/view")
    @FormUrlEncoded
    suspend fun viewExchangeVoucher(
        @Header("Authorization") token: String,
        @Field("products") products: List<String>,
        @Field("prices") prices: List<Int>
    ): GenericResponse<ExchangeVoucherResponse>

    @Multipart
    @POST("api/v1/exchange_voucher")
    @JvmSuppressWildcards
    suspend fun exchangeVoucher(
        @Header("Authorization") token: String,
        @Part image: List<MultipartBody.Part>,
        @Part products: List<MultipartBody.Part>,
        @Part prices: List<MultipartBody.Part>,
        @Part("voucher_id") voucherId: RequestBody,
        @Part("customer_name") customerName: RequestBody,
        @Part("customer_phone") customerPhone: RequestBody,
        @Part("transaction_id") transactionID: RequestBody,
    ): GenericResponse<Any?>

    @GET("api/v1/exchange_voucher/products")
    suspend fun getExchangeProducts(
        @Header("Authorization") token: String
    ): GenericResponse<ExchangeProductsResponse>
}