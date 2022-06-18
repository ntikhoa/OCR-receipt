package com.ntikhoa.ocrreceipt.business.usecase

import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.datasource.network.auth.AuthService
import com.ntikhoa.ocrreceipt.business.domain.model.Account
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AutoLoginUC(
    private val appDataStore: AppDataStore,
    private val authService: AuthService,
) {
    suspend operator fun invoke(): Flow<DataState<Account>> = flow {
        emit(DataState.loading())
        val name = appDataStore.readValue(Constants.DATASTORE_NAME_KEY)
        val token = appDataStore.readValue(Constants.DATASTORE_TOKEN_KEY)

        if (token != null && name != null) {
            val res = authService.autoLogin("Bearer ${token}")
            if (res.message == "valid token") {
                emit(DataState.data(data = Account(token, name)))
            }
        } else {
            emit(DataState.error(Constants.INVALID_TOKEN))
        }
    }.catch {
        emit(handleUseCaseException(it))
    }
}