package com.bannking.app.model.retrofitResponseModel.premiumStatusModel

import com.google.gson.annotations.SerializedName


data class PremiumCheckModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("premium") var premium: String? = null,
    @SerializedName("premium_expire_date") var premiumExpireDate: String? = null,
    @SerializedName("isPremiumUser") var isPremiumUser: Boolean? = null

)