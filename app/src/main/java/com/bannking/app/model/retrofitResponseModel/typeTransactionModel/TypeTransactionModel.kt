package com.bannking.app.model.retrofitResponseModel.typeTransactionModel

import com.google.gson.annotations.SerializedName


data class TypeTransactionModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)


data class Data(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null

)