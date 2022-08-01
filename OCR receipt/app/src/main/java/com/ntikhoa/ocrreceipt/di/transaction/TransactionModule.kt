package com.ntikhoa.ocrreceipt.di.transaction

import com.ntikhoa.ocrreceipt.business.datasource.network.transaction.TransactionService
import com.ntikhoa.ocrreceipt.business.usecase.transaction.GetTransactionsUC
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object TransactionModule {

    @ActivityRetainedScoped
    @Provides
    fun providesTransactionService(retrofitBuilder: Retrofit.Builder): TransactionService {
        return retrofitBuilder.build().create(TransactionService::class.java)
    }

    @ActivityRetainedScoped
    @Provides
    fun providesGetTransactionsUC(service: TransactionService): GetTransactionsUC {
        return GetTransactionsUC(service)
    }
}