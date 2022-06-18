package com.ntikhoa.ocrreceipt.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStore
import com.ntikhoa.ocrreceipt.business.datasource.datastore.AppDataStoreImpl
import com.ntikhoa.ocrreceipt.business.datasource.network.auth.AuthService
import com.ntikhoa.ocrreceipt.business.domain.utils.Constants
import com.ntikhoa.ocrreceipt.presentation.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesGsonConverter(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun providesRetrofitBuilder(gson: Gson): Retrofit.Builder {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    @Provides
    @Singleton
    fun providesAppDataStore(app: Application): AppDataStore {
        return AppDataStoreImpl(app)
    }


    @Provides
    @Singleton
    fun providesSessionManager(): SessionManager {
        return SessionManager()
    }
}