package com.ntikhoa.ocrreceipt.business.usecase.auth

import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.datasource.network.auth.AuthService
import com.ntikhoa.ocrreceipt.business.domain.model.Account
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoginUC(
    private val service: AuthService,
    private val dataStore: AppDataStore,
) {
    suspend operator fun invoke(username: String, password: String): Flow<DataState<Account>> =
        flow {
            emit(DataState.loading())

            val response = service.login(username, password)
            response.data?.let {
                val account = it.toAccount()
                dataStore.setValue(Constants.DATASTORE_TOKEN_KEY, account.token)
                dataStore.setValue(Constants.DATASTORE_NAME_KEY, account.name)
                emit(DataState.data(data = account))
            }
        }.catch {
            emit(handleUseCaseException(it))
        }
}