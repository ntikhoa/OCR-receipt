package com.ntikhoa.ocrreceipt.di.exchangevoucher

import android.content.Context
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ExtractReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.OCRUseCase
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessExtractedReceiptUC
import com.ntikhoa.ocrreceipt.business.usecase.scanreceipt.ProcessImageUC
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ExchangeVoucher {
    @ActivityRetainedScoped
    @Provides
    fun providesProcessImgUseCase(): ProcessImageUC {
        return ProcessImageUC()
    }

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

    @ActivityRetainedScoped
    @Provides
    fun providesProcessExtractedReceiptUC(): ProcessExtractedReceiptUC {
        return ProcessExtractedReceiptUC()
    }
}