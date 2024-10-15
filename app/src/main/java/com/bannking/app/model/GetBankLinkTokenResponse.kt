package com.bannking.app.model

data class GetBankLinkTokenResponse(
    val `data`: GetBankLink,
    val message: String,
    val status: Int
)

data class GetBankLink(
    val expiration: String,
    val link_token: String,
    val request_id: String
)