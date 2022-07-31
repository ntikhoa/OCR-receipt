package com.ntikhoa.ocrreceipt.business.domain.model

import com.ntikhoa.ocrreceipt.business.datasource.network.profile.ProfileResponse

data class AccountProfile(
    val id: Int,
    val name: String,
    val providerName: String,
    val username: String
) {
    companion object {
        fun fromResponse(response: ProfileResponse): AccountProfile {
            return AccountProfile(
                id = response.id,
                name = response.name,
                providerName = response.providerResponse.name,
                username = response.username,
            )
        }
    }
}