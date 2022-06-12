package com.ntikhoa.ocrreceipt.di.extractreceipt

import android.content.Context
import com.ntikhoa.ocrreceipt.business.usecase.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.ExtractReceiptUC
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ExtractReceiptModule {

    @ActivityRetainedScoped
    @Provides
    fun providesOCRUseCase(@ApplicationContext context: Context): OCRUseCase {
        return OCRUseCase(context)
    }

    @ActivityRetainedScoped
    @Provides
    fun providesExtractReceiptUC(): ExtractReceiptUC {
        return ExtractReceiptUC()
    }
}