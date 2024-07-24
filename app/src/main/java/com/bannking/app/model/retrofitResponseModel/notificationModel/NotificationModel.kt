package com.bannking.app.model.retrofitResponseModel.notificationModel

import com.google.gson.annotations.SerializedName

data class NotificationModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)

data class Data(

    @SerializedName("title") var title: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("datetime") var datetime: String? = null

)