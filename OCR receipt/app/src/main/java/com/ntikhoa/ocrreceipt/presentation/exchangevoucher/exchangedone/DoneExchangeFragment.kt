package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.exchangedone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.FragmentDoneExchangeBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DoneExchangeFragment : Fragment(R.layout.fragment_done_exchange) {

    private var _binding: FragmentDoneExchangeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDoneExchangeBinding.bind(view)

        binding.ivEvidence.setImageBitmap(viewModel.evidenceImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}