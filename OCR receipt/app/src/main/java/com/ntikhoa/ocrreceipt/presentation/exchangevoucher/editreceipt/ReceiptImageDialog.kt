package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import com.ntikhoa.ocrreceipt.R
import com.ntikhoa.ocrreceipt.databinding.LayoutImageReceiptBinding

class ReceiptImageDialog(context: Context, val bitmap: Bitmap) :
    Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(context)
        val binding = LayoutImageReceiptBinding.inflate(inflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.ivReceipt.setImageBitmap(bitmap)
        binding.flRoot.setOnClickListener {
            dismiss()
        }
    }
}