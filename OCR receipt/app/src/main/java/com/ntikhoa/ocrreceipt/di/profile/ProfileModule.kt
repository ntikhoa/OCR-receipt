package com.ntikhoa.ocrreceipt.di.profile

import com.ntikhoa.ocrreceipt.business.datasource.network.profile.ProfileService
import com.ntikhoa.ocrreceipt.business.usecase.profile.GetProfileAccountUC
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object ProfileModule {

    @Provides
    @ActivityRetainedScoped
    fun providesProfileService(retrofitBuilder: Retrofit.Builder): ProfileService {
        return retrofitBuilder.build().create(ProfileService::class.java)
    }

    @Provides
    @ActivityRetainedScoped
    fun providesGetProfileAccountUC(service: ProfileService): GetProfileAccountUC {
        return GetProfileAccountUC(service)
    }
}