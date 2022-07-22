package com.ntikhoa.ocrreceipt.business.usecase.exchangevoucher

import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.ExchangeVoucherService
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import com.ntikhoa.ocrreceipt.presentation.SessionManager.token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ViewExchangeVoucherUC(
    private val service: ExchangeVoucherService
) {
    suspend operator fun invoke(
        token: String,
        products: List<String>,
        prices: List<Int>
    ): Flow<DataState<List<Voucher>>> = flow {
        emit(DataState.loading())

        val res = service.viewExchangeVoucher("Bearer $token", products, prices)
        res.data?.vouchers?.let {
            val vouchers = Voucher.fromResponses(it)
            emit(DataState.data(data = vouchers))
        } ?: emit(DataState.data(message = "No Vouchers"))
    }.catch {
        emit(handleUseCaseException(it))
    }
}