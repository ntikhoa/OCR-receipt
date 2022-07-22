package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.exchangedone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.getOutputDir
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.FragmentDoneExchangeBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherEvent
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class DoneExchangeFragment : Fragment(R.layout.fragment_done_exchange) {

    private var _binding: FragmentDoneExchangeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onTriggerEvent(ExchangeVoucherEvent.ExchangeVoucher(requireActivity().getOutputDir()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDoneExchangeBinding.bind(view)

        repeatLifecycleFlow {
            viewModel.exchangeState.collectLatest { dataState ->
                (requireActivity() as ExchangeVoucherActivity).loading(dataState.isLoading)

                dataState.message?.let {
                    binding.tvRes.text = it
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}