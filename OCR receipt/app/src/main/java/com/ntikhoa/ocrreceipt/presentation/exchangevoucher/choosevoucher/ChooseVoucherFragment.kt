package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onTriggerEvent(ExchangeVoucherEvent.ViewExchangeVoucher)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChooseVoucherBinding.bind(view)

        initRecyclerView()

        checkEnableDone()

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
                findNavController().navigate(R.id.action_chooseVoucherFragment_to_getUserInfoFragment)
            }
        }
    }

    private fun checkEnableDone() {
        viewModel.voucher?.let {
            setEnableDone(true, 1.0f)
        } ?: run {
            setEnableDone(false, 0.5f)
        }
    }

    private fun setEnableDone(isEnabled: Boolean, alpha: Float) {
        binding.btnDone.apply {
            this.isEnabled = isEnabled
            this.alpha = alpha
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            adapter = VoucherAdapter(viewModel.voucher?.ID)
            rvVoucher.adapter = adapter
            
            adapter.setOnItemClickListener { position ->
                if (viewModel.submitVoucher(position) != null) {
                    setEnableDone(true, 1.0f)
                }
            }

            adapter.setOnInfoClickListener { voucher ->
                VoucherDetailDialog(requireActivity(), voucher).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvVoucher.adapter = null
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.voucher = null
    }
}