package com.ntikhoa.ocrreceipt.presentation.transaction.transactiondetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivityTransactionDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TransactionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailBinding

    private val viewModel by viewModels<TransactionDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repeatLifecycleFlow {
            viewModel.state.collectLatest { dataState ->

                //TODO: display progress bar when loading
                dataState.isLoading

                dataState.transactionDetail?.let {
                    //TODO: bind data to view
                    println(it.voucher.giftName)
                }

                dataState.message?.let {
                    // display error message
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val id = intent.getIntExtra("id", -1)
        if (id != -1) {
            viewModel.onTriggerEvent(TransactionDetailEvent.GetTransactionDetail(id))
        }
    }
}