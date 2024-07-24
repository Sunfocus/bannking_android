package com.bannking.app.model.retrofitResponseModel.accountListModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountListModel(
    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()
) : Serializable


class Data : Serializable {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("acc_detail")
    var accDetail: ArrayList<AccDetail> = arrayListOf()

    @SerializedName("user_id")
    var userId: String? = null

    @SerializedName("acc_menu_id")
    var accMenuId: String? = null

    @SerializedName("acc_menu_title")
    var accMenuTitle: String? = null

    @SerializedName("budget_id")
    var budgetId: String? = null

    @SerializedName("budget_title")
    var budgetTitle: String? = null

    @SerializedName("account")
    var account: String? = null

    @SerializedName("account_code")
    var accountCode: String? = null

    @SerializedName("currency_id")
    var currencyId: String? = null

    @SerializedName("currency")
    var currency: String? = null

    @SerializedName("amount")
    var amount: String? = null


}


data class AccDetail(

    @SerializedName("acc_menu_id") var accMenuId: String? = null,
    @SerializedName("acc_menu_title") var accMenuTitle: String? = null

) : Serializable