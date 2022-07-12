package com.ntikhoa.ocrreceipt.business.usecase.scanreceipt

import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProcessExtractedReceiptUC {

    suspend operator fun invoke(receipt: Receipt): Flow<DataState<Receipt>> = flow {
        emit(DataState.loading())

        val products = receipt.products.map {
            autoCorrectZeroAndCharO(it.trim().uppercase())
        }

        val prices = receipt.prices.map {
            it.replace(" ", "").replace(".", ",")
        }

        emit(
            DataState.data(
                data =
                receipt.copy(
                    products = products.toMutableList(),
                    prices = prices.toMutableList()
                )
            )
        )
    }.catch {
        emit(handleUseCaseException(it))
    }

    private suspend fun autoCorrectZeroAndCharO(product: String): String {
        var result = product[0].toString()
        for (i in 1 until product.length) {
            if (product[i] == 'O' || product[i] == 'o') {
                if (result.last().isDigit()) {
                    result += '0'
                }
            } else if (product[i] == '0') {
                if (!result.last().isDigit()) {
                    result += 'O'
                }
            } else {
                result += product[i]
            }
        }
        return result
    }

    private suspend fun removeSpaceInPrices(price: String): String {
        return price.replace(" ", "")
    }
}