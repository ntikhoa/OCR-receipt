package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.choosevoucher

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import com.ntikhoa.ocrreceipt.business.domain.model.Voucher
import com.ntikhoa.ocrreceipt.databinding.LayoutVoucherDetailDialogBinding

class VoucherDetailDialog(context: Context, val voucher: Voucher) :
    Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)
        val binding = LayoutVoucherDetailDialogBinding.inflate(inflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.layoutVoucher.apply {
            tvVoucherName.text = voucher.Name
            tvGiftName.text = voucher.GiftName
            tvDescription.text = voucher.Description
        }
        binding.flRoot.setOnClickListener {
            dismiss()
        }
    }
}