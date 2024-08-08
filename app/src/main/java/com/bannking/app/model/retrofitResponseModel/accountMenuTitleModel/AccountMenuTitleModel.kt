package com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel

import com.bannking.app.utils.Constants
import com.google.gson.annotations.SerializedName


data class AccountTitleModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)

data class Data(

//    AccountMenuId
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("type") var type: String? = null,
    var budgetName: String? = null,
    var accountName: String? = null,
    var account_code: String? = null,
    var accountAmount: String? = null,
    var budgetId: String? = null,
    var isFillUp: Boolean = false,
    var isSelected: Boolean = false,
    var isAccountCreated: Int = 0,
    @SerializedName("isTitleMenuHasAccount") var isTitleMenuHasAccount: Boolean = false,
    @SerializedName("accountType") var accountType: String = Constants.ACCOUNT_NORMAL

)