package com.bannking.app.model.retrofitResponseModel.otpModel

import com.google.gson.annotations.SerializedName


data class OtpModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("otp") var otp: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("user_id") var user_id: String? = null

)