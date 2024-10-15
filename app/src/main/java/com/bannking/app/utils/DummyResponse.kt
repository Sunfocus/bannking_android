package com.bannking.app.utils

import com.bannking.app.model.BankListData

data class DummyResponse(
    val `data`: ArrayList<BankListData>,
    val message: String,
    val status: Int
)

data class BankListData(
    val accountsData: ArrayList<AccountsData>,
    val institutionId: String,
    val institutionName: String
)

data class AccountsData(
    val accountId: String,
    val accountName: String,
    val accountNumber: String,
    val accountType: String,
    val balance: Int,
    val lastUpdatedDatetime: String
)