package com.ntikhoa.ocrreceipt.business.domain.utils

data class DataState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> loading(): DataState<T> = DataState(isLoading = true)

        fun <T> error(
            message: String
        ): DataState<T> {
            return DataState(
                message = message
            )
        }


        fun <T> data(
            message: String? = null,
            data: T? = null
        ): DataState<T> {
            return DataState(
                message = message,
                data = data
            )
        }
    }
}