package com.ntikhoa.ocrreceipt.business.usecase

import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import retrofit2.HttpException
import java.lang.Exception

fun <T> handleUseCaseException(
    e: Throwable,
    errorHandler: (HttpException) -> DataState<T>
): DataState<T> {
    e.printStackTrace()
    return when (e) {
        is HttpException -> {
            try {
                errorHandler(e)
            } catch (customException: Throwable) {
                DataState(message = convertErrorBody(e))
            }
        }
        else -> {
            DataState(message = Constants.UNKNOWN_ERROR)
        }
    }
}

fun <T> handleUseCaseException(
    e: Throwable
): DataState<T> {
    e.printStackTrace()
    return when (e) {
        is HttpException -> {
            DataState(message = convertErrorBody(e))
        }
        else -> {
            DataState(message = Constants.UNKNOWN_ERROR)
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String {
    return try {
        throwable.response()?.errorBody()?.string()
            ?: Constants.UNKNOWN_ERROR
    } catch (exception: Exception) {
        Constants.UNKNOWN_ERROR
    }
}