package com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel

import com.bannking.app.utils.Constants
import com.google.gson.annotations.SerializedName


data class AccountTitleModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)

data class Data(

//    AccountMenuId
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("type") var type: String? = null,
    var budgetName: String? = null,
    var accountName: String? = null,
    var accountCode: String? = null,
    var accountAmount: String? = null,
    var budgetId: String? = null,
    var isFillUp: Boolean = false,
    var isSelected: Boolean = false,
    var isAccountCreated: Boolean = false,
    @SerializedName("isTitleMenuHasAccount") var isTitleMenuHasAccount: Boolean = false,
    @SerializedName("accountType") var accountType: String = Constants.ACCOUNT_NORMAL

)