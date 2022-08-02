package com.ntikhoa.ocrreceipt.presentation.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.databinding.ActivityHomeBinding
import com.ntikhoa.ocrreceipt.presentation.auth.LoginActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.profile.ProfileActivity
import com.ntikhoa.ocrreceipt.presentation.transaction.transactionlist.TransactionHistoryActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvHello.text = viewModel.getFirstName()

            cardPhoto.setOnClickListener {
                val exchangeVoucherIntent =
                    Intent(applicationContext, ExchangeVoucherActivity::class.java)
                startActivity(exchangeVoucherIntent)
            }

            cardProfile.setOnClickListener {
                val profileIntent = Intent(applicationContext, ProfileActivity::class.java)
                startActivity(profileIntent)
            }

            cardHistory.setOnClickListener {
                val historyIntent =
                    Intent(applicationContext, TransactionHistoryActivity::class.java)
                startActivity(historyIntent)
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (!viewModel.isAuth()) {
            val loginIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}