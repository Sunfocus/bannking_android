package com.bannking.app.model.retrofitResponseModel.budgetPlannerModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BudgetPlannerModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

) : Serializable

data class Data(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean = false

) : Serializable