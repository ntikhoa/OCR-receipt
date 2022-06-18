package com.ntikhoa.ocrreceipt.presentation.splash

sealed class SplashState() {
    object AutoLoginSuccess : SplashState()
    object AutoLoginFail : SplashState()
    object None : SplashState()
}