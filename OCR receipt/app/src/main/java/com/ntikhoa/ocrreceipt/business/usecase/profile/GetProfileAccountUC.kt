package com.ntikhoa.ocrreceipt.business.usecase.profile

import com.ntikhoa.ocrreceipt.business.datasource.network.profile.ProfileService
import com.ntikhoa.ocrreceipt.business.domain.model.AccountProfile
import com.ntikhoa.ocrreceipt.business.domain.utils.DataState
import com.ntikhoa.ocrreceipt.business.usecase.handleUseCaseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetProfileAccountUC(
    private val service: ProfileService
) {
    suspend operator fun invoke(token: String): Flow<DataState<AccountProfile>> = flow {
        emit(DataState.loading())

        val res = service.getProfileAccount("Bearer $token")
        res.data?.let {
            val profile = AccountProfile.fromResponse(it.profileResponse)
            emit(DataState.data(data = profile))

        } ?: run {
            emit(DataState.error<AccountProfile>(res.message))
        }

    }.catch {
        emit(handleUseCaseException(it))
    }
}