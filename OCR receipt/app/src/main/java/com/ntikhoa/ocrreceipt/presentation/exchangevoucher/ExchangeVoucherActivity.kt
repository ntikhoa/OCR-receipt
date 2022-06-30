package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeVoucherActivity : AppCompatActivity() {

    private val viewModel by viewModels<ExchangeVoucherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange_voucher)
    }
}