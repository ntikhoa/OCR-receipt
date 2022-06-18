package com.ntikhoa.ocrreceipt.di.auth

import android.app.Application
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStoreImpl
import com.ntikhoa.ocrreceipt.business.datasource.network.auth.AuthService
import com.ntikhoa.ocrreceipt.business.usecase.AutoLoginUC
import com.ntikhoa.ocrreceipt.business.usecase.LoginUC

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthModule {


    @Provides
    @ActivityRetainedScoped
    fun providesLoginUC(service: AuthService, dataStore: AppDataStore): LoginUC {
        return LoginUC(service, dataStore)
    }

    @Provides
    @ActivityRetainedScoped
    fun providesAuthService(retrofitBuilder: Retrofit.Builder): AuthService {
        return retrofitBuilder.build().create(AuthService::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun providesAutoLoginUC(dataStore: AppDataStore, authService: AuthService): AutoLoginUC {
        return AutoLoginUC(dataStore, authService)
    }
}