package com.bannking.app.model.retrofitResponseModel.headerModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class HeaderModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

) : Serializable

data class Data(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
) : Serializable