package com.ntikhoa.ocrreceipt.business.usecase.exchangevoucher

import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.ExchangeVoucherService
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import com.ntikhoa.ocrreceipt.presentation.SessionManager.token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ExchangeVoucherUC(private val service: ExchangeVoucherService) {

    suspend operator fun invoke(
        token: String,
        image: List<MultipartBody.Part>,
        products: List<MultipartBody.Part>,
        prices: List<MultipartBody.Part>,
        voucherId: RequestBody,
        customerName: RequestBody,
        customerPhone: RequestBody,
        transactionID: RequestBody,
    ): Flow<DataState<String>> = flow {

        emit(DataState.loading())

        val res = service.exchangeVoucher(
            "Bearer $token",
            image,
            products,
            prices,
            voucherId,
            customerName,
            customerPhone,
            transactionID
        )

        emit(DataState.data(data = res.message))
    }.catch {
        emit(handleUseCaseException(it))
    }

}

