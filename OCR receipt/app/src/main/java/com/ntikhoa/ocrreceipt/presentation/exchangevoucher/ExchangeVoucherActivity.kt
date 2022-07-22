package com.ntikhoa.ocrreceipt.presentation.exchangevoucher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.ActivityExchangeVoucherBinding
import com.ntikhoa.ocrreceipt.presentation.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeVoucherActivity : AppCompatActivity() {

    private var _binding: ActivityExchangeVoucherBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ExchangeVoucherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityExchangeVoucherBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun loading(isLoading: Boolean) {
        binding.progressBar.setVisibility(isLoading)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}