package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.repeatLifecycleFlow
import com.ntikhoa.ocrreceipt.databinding.FragmentEditReceiptBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherActivity
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherEvent
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class EditReceiptFragment : Fragment(R.layout.fragment_edit_receipt) {

    private var _binding: FragmentEditReceiptBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditReceiptBinding.bind(view)

        requireActivity().repeatLifecycleFlow {
            viewModel.state.collectLatest {
                (requireActivity() as ExchangeVoucherActivity).loading(it.isLoading)

                it.receipt?.let { receipt ->
                    binding.tvReceipt.text = receipt.visionText
                }
            }
        }

        viewModel.onTriggerEvent(ExchangeVoucherEvent.ScanReceipt(viewModel.croppedImage!!))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}