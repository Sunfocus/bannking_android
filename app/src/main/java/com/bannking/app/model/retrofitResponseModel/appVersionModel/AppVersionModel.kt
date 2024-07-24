package com.bannking.app.model.retrofitResponseModel.appVersionModel

import com.google.gson.annotations.SerializedName


data class AppVersionModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: AppVersionChekData? = AppVersionChekData()

)

data class AppVersionChekData(

    @SerializedName("android_version") var androidVersion: Double? = null,
    @SerializedName("ios_version") var iosVersion: Double? = null,
    @SerializedName("android_maintenance") var androidMaintenance: Boolean? = null,
    @SerializedName("ios_maintenance") var iosMaintenance: Boolean? = null,
    @SerializedName("android_url") var androidUrl: Boolean? = null,
    @SerializedName("ios_url") var iosUrl: Boolean? = null,
    @SerializedName("logo") var logo: String? = null,
    @SerializedName("privacy_url") var privacyUrl: String? = null

)