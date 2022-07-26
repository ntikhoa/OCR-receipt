package com.ntikhoa.ocrreceipt.business.usecase.fulltextsearch

import com.ntikhoa.ocrreceipt.business.datasource.network.exchangevoucher.ExchangeVoucherService
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.process.VectorSpaceModel
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class BuildDocTermMatrixUC(
    private val service: ExchangeVoucherService,
    private val model: VectorSpaceModel
) {
    suspend operator fun invoke(token: String): Flow<DataState<Map<String, Map<String, Float>>>> =
        flow {
            emit(DataState.loading())

            val res = service.getExchangeProducts("Bearer $token")
            res.data?.let {

                val docTermMatrix = model(it.productsNames)
                emit(DataState.data(data = docTermMatrix))

            } ?: emit(DataState.error(res.message))

        }.catch {
            emit(handleUseCaseException(it))
        }
}