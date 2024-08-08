package com.bannking.app.model.retrofitResponseModel.otpModel

import com.google.gson.annotations.SerializedName


data class OtpModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = null,


)
data class Data(
    @SerializedName("otp") var otp: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("id") var id: String? = null
)