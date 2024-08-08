package com.bannking.app.model.retrofitResponseModel.notificationModel


import com.google.gson.annotations.SerializedName

data class NotificationModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: Data = Data()


    )

data class Data(
    @SerializedName("currentPage") var currentPage: Int? = null,
    @SerializedName("pageSize") var pageSize: Int? = null,
    @SerializedName("totalRecords") var totalRecords: Int? = null,
    @SerializedName("totalPages") var totalPages: Int? = null,
    @SerializedName("notifications") var notifications: ArrayList<Notification>? = null,

)
data class Notification(
    @SerializedName("createdAt") var createdAt: String,
    @SerializedName("details") var details: String,
    @SerializedName("id") var id: String,
    @SerializedName("image") var image: String,
    @SerializedName("title") var title: String,
)