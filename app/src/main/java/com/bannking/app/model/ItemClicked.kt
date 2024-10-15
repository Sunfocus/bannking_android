package com.bannking.app.model

interface ItemClicked {
    fun onClickedBankItem(
        position: Int,
        institutionId: String,
        accountNumber: String,
        balance: Int,
        accountName: String,
        accountId: String
    )
}
interface OnClickedItems{
    fun clickedPassChildBankData(
        position: Int,
        institutionId: String,
        accountNumber: String,
        balance: Int,
        accountName: String,
        accountId: String
    )
}