package com.bannking.app.model.retrofitResponseModel.accountListModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountListModel(
    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()
) : Serializable


class Data : Serializable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("userAccountTitle")
    var userAccountTitle: UserAccountTitle?= null

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


data class UserAccountTitle(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null

) : Serializable
data class Currency(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("icon") var icon: String? = null

) : Serializable