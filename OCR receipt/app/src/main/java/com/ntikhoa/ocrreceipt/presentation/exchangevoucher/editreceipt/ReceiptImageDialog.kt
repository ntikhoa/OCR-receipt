package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ntikhoa.ocrreceipt.databinding.LayoutImageReceiptDialogBinding

class ReceiptImageDialog(context: Context, val bitmap: Bitmap) :
    Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)
        val binding = LayoutImageReceiptDialogBinding.inflate(inflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.ivReceipt.setImageBitmap(bitmap)
        binding.flRoot.setOnClickListener {
            dismiss()
        }
    }
}