package com.ntikhoa.ocrreceipt.business.datasource.network.transaction

import com.ntikhoa.ocrreceipt.business.datasource.network.GenericResponse
import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.responses.TransactionResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface TransactionService {

    @GET("api/v1/transactions")
    suspend fun getTransaction(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): GenericResponse<TransactionResponse>
}