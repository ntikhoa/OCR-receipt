package com.ntikhoa.ocrreceipt.business.datasource.network.profile

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.response.ExchangeVoucherResponse
import retrofit2.http.*

interface ProfileService {
    @GET("api/v1/accounts")
    suspend fun getProfileAccount(
        @Header("Authorization") token: String,
    ): GenericResponse<GetProfileResponse>
}