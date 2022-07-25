package com.ntikhoa.ocrreceipt.presentation.exchangevoucher.editreceipt

interface OnEditReceiptList {
    fun onDone(position: Int, text: String)
    fun onRemove(position: Int)
    fun onAddTop(position: Int)
    fun onAddBottom(position: Int)
}

interface SetEditReceiptListListener {
    fun setOnEditReceiptListListener(onEdit: OnEditReceiptList)
}