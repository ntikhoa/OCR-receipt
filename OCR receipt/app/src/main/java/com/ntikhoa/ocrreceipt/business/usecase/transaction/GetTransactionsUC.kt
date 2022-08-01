package com.ntikhoa.ocrreceipt.business.usecase.transaction

import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.TransactionService
import com.ntikhoa.ocrreceipt.business.domain.model.Transaction
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTransactionsUC(
    private val service: TransactionService,
) {
    suspend operator fun invoke(
        token: String,
        page: Int,
        perPage: Int
    ): Flow<DataState<List<Transaction>>> = flow {
        emit(DataState.loading())

        val res = service.getTransaction("Bearer $token", page, perPage)
        res.data?.let {
            val receipts = Transaction.fromListResponse(it.receipts)
            emit(DataState.data(data = receipts))
        } ?: run {
            emit(DataState.error<List<Transaction>>(res.message))
        }


    }.catch {
        emit(handleUseCaseException(it))
    }
}