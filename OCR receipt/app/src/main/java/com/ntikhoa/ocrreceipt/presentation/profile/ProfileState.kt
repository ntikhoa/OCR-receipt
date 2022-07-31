package com.ntikhoa.ocrreceipt.presentation.profile

import com.ntikhoa.ocrreceipt.business.domain.model.AccountProfile

data class ProfileState(
    var isLoading: Boolean = false,
    var profile: AccountProfile? = null,
    var message: String? = null,
)