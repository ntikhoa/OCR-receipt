package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.getuserinfo

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.databinding.FragmentGetUserInfoBinding
import com.ntikhoa.ocrreceipt.presentation.exchangevoucher.ExchangeVoucherViewModel


class GetUserInfoFragment : Fragment(R.layout.fragment_get_user_info) {

    private var _binding: FragmentGetUserInfoBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<ExchangeVoucherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGetUserInfoBinding.bind(view)

        viewModel.customerInfo.let {
            binding.apply {
                etCustomerName.setText(it.customerName)
                etCustomerPhone.setText(it.customerPhone)
                etTransactionId.setText(it.transactionID)
            }
        }

        checkEnableDone()

        viewModel.voucher?.let {
            setVoucherInfo(it)
        }

        binding.apply {
            btnDone.setOnClickListener {
                findNavController().navigate(R.id.action_getUserInfoFragment_to_takeEvidenceFragment)
            }
        }

        binding.apply {
            etCustomerName.addTextChangedListener {
                viewModel.customerInfo.customerName = it.toString()
                checkEnableDone()
            }

            etCustomerPhone.addTextChangedListener {
                viewModel.customerInfo.customerPhone = it.toString()
                checkEnableDone()
            }

            etTransactionId.addTextChangedListener {
                viewModel.customerInfo.transactionID = it.toString()
                checkEnableDone()
            }
        }
    }

    private fun checkEnableDone() {
        if (viewModel.customerInfo.isNotEmpty()) {
            setEnableDone(true, 1.0f)
        } else setEnableDone(false, 0.5f)
    }

    private fun setEnableDone(isEnabled: Boolean, alpha: Float) {
        binding.btnDone.apply {
            this.isEnabled = isEnabled
            this.alpha = alpha
        }
    }

    private fun setVoucherInfo(voucher: Voucher) {
        binding.layoutVoucher.apply {
            tvVoucherName.text = voucher.name
            tvGiftName.text = voucher.giftName
            tvDescription.text = voucher.description
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}