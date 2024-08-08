package com.bannking.app.model.retrofitResponseModel.updateAccountModel


import com.google.gson.annotations.SerializedName


data class UpdateAccountModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
//    @SerializedName("account") var account: String? = null,
//    @SerializedName("account_code") var account_code: String? = null,
//    @SerializedName("amount") var amount: String? = null,
    @SerializedName("data") var data: Data? = null,
    @SerializedName("extraData") var extraData: String? = null

)
data class Data(
    @SerializedName("account") var account: String? = null,
    @SerializedName("account_code") var account_code: String? = null,
    @SerializedName("amount") var amount: String? = null
)