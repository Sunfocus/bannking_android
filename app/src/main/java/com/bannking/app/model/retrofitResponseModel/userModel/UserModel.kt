package com.bannking.app.model.retrofitResponseModel.userModel

import com.google.gson.annotations.SerializedName


data class UserModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = Data()


)

data class Data(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("language_id") var languageId: Int? = null,
    @SerializedName("language_name") var languageName: String? = null,
    @SerializedName("currency_id") var currencyId: Int? = null,
    @SerializedName("currency_name") var currencyName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("notification") var notification: String? = null,
    @SerializedName("premium") var premium: Boolean? = null

)