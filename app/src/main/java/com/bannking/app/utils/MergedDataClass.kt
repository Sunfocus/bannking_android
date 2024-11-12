package com.bannking.app.utils

import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData
import com.bannking.app.model.retrofitResponseModel.accountListModel.BudgetPlanner
import com.bannking.app.model.retrofitResponseModel.accountListModel.Currency
import com.bannking.app.model.retrofitResponseModel.accountListModel.UserAccountTitle
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MergedDataClass(
    @SerializedName("id") var id: String? = null,

    @SerializedName("userAccountTitle") var userAccountTitle: UserAccountTitle? = null,

    @SerializedName("budgetPlanner") var budgetPlanner: BudgetPlanner? = null,

    @SerializedName("user_id") var user_id: String? = null,

    @SerializedName("acc_title_id") var acc_title_id: String? = null,

    @SerializedName("acc_menu_title") var accMenuTitle: String? = null,

    @SerializedName("budget_id") var budget_id: String? = null,

    @SerializedName("budget_title") var budgetTitle: String? = null,

    @SerializedName("account") var account: String? = null,

    @SerializedName("account_code") var account_code: String? = null,

    @SerializedName("currency_id") var currencyId: String? = null,

    @SerializedName("currency") var currency: Currency? = null,

    @SerializedName("amount") var amount: String? = null,

    @SerializedName("accountTitleId") val accountTitleId: Int ? = null,
    @SerializedName("accountsData") val accountsData: List<AccountsData>? = null,
    @SerializedName("institutionId") val institutionId: String? = null,
    @SerializedName("institutionName") val institutionName: String? = null
) : Serializable
