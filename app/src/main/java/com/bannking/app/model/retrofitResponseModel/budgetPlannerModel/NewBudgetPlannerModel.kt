package com.bannking.app.model.retrofitResponseModel.budgetPlannerModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewBudgetPlannerModel (@SerializedName("status") var status: Int? = null,
                                  @SerializedName("data") var data: NewData

) : Serializable

data class NewData(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("isSelected") var isSelected: Boolean = false

) : Serializable
