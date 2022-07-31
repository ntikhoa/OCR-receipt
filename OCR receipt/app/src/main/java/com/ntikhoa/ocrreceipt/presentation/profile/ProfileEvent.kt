package com.ntikhoa.ocrreceipt.presentation.profile

sealed class ProfileEvent {
    object GetProfileAccount: ProfileEvent()
}