package com.bannking.app.model.retrofitResponseModel.updateAccountModel


import com.google.gson.annotations.SerializedName


data class UpdateAccountModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("account") var account: String? = null,
    @SerializedName("account_code") var accountCode: String? = null,
    @SerializedName("amount") var amount: String? = null

)