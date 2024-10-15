package com.bannking.app.model

data class ExchangeTokenResponse(
    val `data`: ExchangeData,
    val message: String,
    val status: Int
)

data class ExchangeData(
    val access_token: String,
    val accountsArray: ArrayList<AccountsArray>,
    val bank_name: String,
    val institutionId: String
)

data class AccountsArray(
    val accountId: String,
    val accountName: String,
    val accountNumber: String,
    val accountType: String,
    val balance: Int
)