package com.bannking.app.model

data class GetBankBalanceSpendResponse(
    val `data`: GetBankBalanceSpendData,
    val message: String,
    val status: Int
)

data class GetBankBalanceSpendData(
    val accountDetails: AccountDetails,
    val currentMonthTotalSpending: Double,
    val institutionName: String,
    val month: String
)

data class AccountDetails(
    val accountId: String,
    val accountName: String,
    val accountNumber: String,
    val accountType: String,
    val balance: Int
)