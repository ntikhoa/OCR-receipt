package com.ntikhoa.ocrreceipt.business.usecase.scanreceipt

import android.service.autofill.Validators.not
import com.ntikhoa.ocrreceipt.business.domain.model.Receipt
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ProcessExtractedReceiptUC {

    private val khuyenmaiDic = mapOf(
        ".HUYN" to true,
        "KHUYẾN" to true,
        "KHUYEN" to true,
        "KHEYEN" to true,
        "KHUYEN" to true,
        "HUYEN" to true,
        "KHUYER" to true,
        "KHUVEN" to true,
        "KHUYEN" to true,
        "NHUYN" to true,
        "KHUYER" to true,
        "MÃI" to true,
        "MAI" to true,
        "MÄI" to true,
        "AI" to true,
        "MÁI" to true,
    )

    suspend operator fun invoke(receipt: Receipt): Flow<DataState<Receipt>> = flow {
        emit(DataState.loading())

        val products = receipt.products.map {
            autoCorrectZeroAndCharO(it.trim().uppercase())
        }.filter {
            val word = it.split(" ")
            if (word.size > 1) {
                khuyenmaiDic[word[0]] == null || khuyenmaiDic[word[1]] == null
            } else true
        }

        val prices = receipt.prices.map {
            it.replace(" ", "")
                .replace(".", ",")
                .replace("O", "0")
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
                result += if (result.last().isDigit()) {
                    '0'
                } else product[i]
            } else if (product[i] == '0') {
                result += if (!result.last().isDigit()) {
                    'O'
                } else product[i]
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