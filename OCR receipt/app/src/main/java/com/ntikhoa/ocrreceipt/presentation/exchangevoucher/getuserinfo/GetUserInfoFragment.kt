package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.getuserinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        viewModel.voucher?.let {
            setVoucherInfo(it)
        }
    }

    private fun setVoucherInfo(voucher: Voucher) {
        binding.apply {
            tvVoucherName.text = voucher.Name
            tvGiftName.text = voucher.GiftName
            tvDescription.text = voucher.Description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}