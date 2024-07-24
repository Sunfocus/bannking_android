package com.bannking.app.model.retrofitResponseModel.exploreExpensesModel

import com.google.gson.annotations.SerializedName


data class ExploreExpensesModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)

data class Data(

    @SerializedName("name") var name: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("value") var value: String? = null
)

