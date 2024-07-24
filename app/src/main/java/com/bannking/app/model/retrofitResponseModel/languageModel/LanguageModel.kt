package com.bannking.app.model.retrofitResponseModel.languageModel

import com.google.gson.annotations.SerializedName


data class LanguageModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data>? = arrayListOf()


)

data class Data(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("img") var img: String? = null,
    @SerializedName("rate") var rate: String? = null,
    var isSelected: Boolean = false

)