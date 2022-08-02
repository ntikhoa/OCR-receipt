package com.ntikhoa.ocrreceipt.business.usecase.transaction

import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.TransactionService
import com.ntikhoa.ocrreceipt.business.domain.model.TransactionDetail
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import com.ntikhoa.ocrreceipt.presentation.SessionManager.token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTransactionDetailUC(
    private val service: TransactionService
) {
    suspend operator fun invoke(token: String, id: Int): Flow<DataState<TransactionDetail>> = flow {

        emit(DataState.loading())

        val res = service.getTransactionDetail("Bearer $token", id)
        res.data?.let {
            val transactionDetail = TransactionDetail.fromResponse(it.receiptDetailResponse)
            emit(DataState.data(data = transactionDetail))
        } ?: run {
            emit(DataState.error<TransactionDetail>(res.message))
        }

    }.catch {
        emit(handleUseCaseException(it))
    }
}