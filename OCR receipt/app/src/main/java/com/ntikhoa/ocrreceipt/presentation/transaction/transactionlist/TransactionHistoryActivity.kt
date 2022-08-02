package com.ntikhoa.ocrreceipt.presentation.transaction.transactionlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.ActivityTransactionHistoryBinding
import com.ntikhoa.ocrreceipt.presentation.setVisibility
import com.ntikhoa.ocrreceipt.presentation.transaction.transactiondetail.TransactionDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding

    private val viewModel by viewModels<TransactionViewModel>()

    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        repeatLifecycleFlow {
            viewModel.state.collectLatest { dataState ->
                binding.progressBar.setVisibility(dataState.loading)

                dataState.transactions?.let {
                    adapter.submitList(it)
                }
            }
        }

        viewModel.onTriggerEvent(TransactionEvent.GetListTransaction)

    }

    private fun initRecyclerView() {
        adapter = TransactionAdapter()
        binding.apply {
            rvTransaction.adapter = adapter
            rvTransaction.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == adapter.itemCount.minus(1)
                        && !viewModel.state.value.loading
                        && !viewModel.state.value.isExhausted
                    ) {
                        viewModel.onTriggerEvent(TransactionEvent.GetListTransactionNextPage)
                    }
                }
            })
        }
        adapter.setOnItemClickListener { position, item ->
            val detailIntent = Intent(applicationContext, TransactionDetailActivity::class.java)
            detailIntent.putExtra("id", item.id)
            startActivity(detailIntent)
        }
    }
}