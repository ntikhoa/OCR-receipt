package com.ntikhoa.ocrreceipt.presentation

interface OnTriggerEvent<T> {
    fun onTriggerEvent(event: T)
}