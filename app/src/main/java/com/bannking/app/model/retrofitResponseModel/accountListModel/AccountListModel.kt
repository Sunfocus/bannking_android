package com.bannking.app.model.retrofitResponseModel.accountListModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountListModel(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("extraData") var extraData: ArrayList<ExtraData> = arrayListOf(),
    @SerializedName("hiddenData") var hiddenData: ArrayList<HiddenData> = arrayListOf()
) : Serializable


class Data : Serializable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("userAccountTitle")
    var userAccountTitle: UserAccountTitle? = null

    @SerializedName("budgetPlanner")
    var budgetPlanner: BudgetPlanner? = null

    @SerializedName("user_id")
    var user_id: String? = null

    @SerializedName("acc_title_id")
    var acc_title_id: String? = null

    @SerializedName("acc_menu_title")
    var accMenuTitle: String? = null

    @SerializedName("budget_id")
    var budget_id: String? = null

    @SerializedName("budget_title")
    var budgetTitle: String? = null

    @SerializedName("account")
    var account: String? = null

    @SerializedName("account_code")
    var account_code: String? = null

    @SerializedName("currency_id")
    var currencyId: String? = null

    @SerializedName("currency")
    var currency: Currency? = null

    @SerializedName("amount")
    var amount: String? = null


}


data class HiddenData(
    @SerializedName("accountId")val accountId: String,
    @SerializedName("createdAt")val createdAt: String,
    @SerializedName("id")val id: Int,
    @SerializedName("instituteId")val instituteId: String,
    @SerializedName("updatedAt")val updatedAt: String,
    @SerializedName("userId")val userId: Int
) : Serializable

data class ExtraData(
    @SerializedName("accountTitleId") var accountTitleId: Int,
    @SerializedName("accountsData") val accountsData: ArrayList<AccountsData>,
    @SerializedName("institutionId") val institutionId: String,
    @SerializedName("institutionName") val institutionName: String
) : Serializable

data class AccountsData(
    @SerializedName("accountId") val accountId: String,
    @SerializedName("accountName") val accountName: String,
    @SerializedName("accountNumber") val accountNumber: String,
    @SerializedName("accountType") val accountType: String,
    @SerializedName("balance") val balance: Int
): Serializable
data class BudgetPlanner(
    @SerializedName("id") var id: String? = null, @SerializedName("name") var name: String? = null
) : Serializable

data class UserAccountTitle(
    @SerializedName("id") var id: String? = null, @SerializedName("name") var name: String? = null

) : Serializable

data class Currency(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("icon") var icon: String? = null
) : Serializable