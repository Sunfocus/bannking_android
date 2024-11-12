package com.bannking.app.model

import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData

interface ItemClicked {
    fun onClickedBankItem(
        position: Int,
        institutionId: String,
        accountNumber: String,
        balance: Int,
        accountName: String,
        accountId: String
    )
    fun onClickedBankItemVoice(accountsData: AccountsData)
    fun onClickedBankItemMore(
        accountsId: String,
        institutionId: String,
        accountsList: ArrayList<AccountsData>,
        extraDataHideList: ArrayList<AccountsData>
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