package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.FragmentChooseVoucherBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherEvent
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChooseVoucherFragment : Fragment(R.layout.fragment_choose_voucher) {

    private var _binding: FragmentChooseVoucherBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: VoucherAdapter

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    private var voucherIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onTriggerEvent(ExchangeVoucherEvent.ViewExchangeVoucher)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseVoucherBinding.bind(view)

        initRecyclerView()

        repeatLifecycleFlow {
            viewModel.viewExchangeState.collectLatest { dataState ->
                (activity as ExchangeVoucherActivity).loading(dataState.isLoading)

                dataState.voucher?.let { vouchers ->
                    adapter.submitList(vouchers)
                }

                dataState.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.apply {
            btnDone.setOnClickListener {
                if (voucherIndex != -1 && viewModel.submitVoucher(voucherIndex) != null) {
                    println(viewModel.voucher?.Name)
                    findNavController().navigate(R.id.action_chooseVoucherFragment_to_getUserInfoFragment)
                } else {
                    Toast.makeText(context, "Vui lòng chọn khuyến mãi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            adapter = VoucherAdapter()
            rvVoucher.adapter = adapter
            adapter.listener = object : VoucherAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    voucherIndex = position
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvVoucher.adapter = null
        _binding = null
    }
}