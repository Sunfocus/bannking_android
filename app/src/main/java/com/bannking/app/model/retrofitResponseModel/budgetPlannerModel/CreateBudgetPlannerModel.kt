package com.bannking.app.model.retrofitResponseModel.budgetPlannerModel

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BudgetPlannerModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

) : Serializable

data class Data(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("color") var color: String? = null,
    @SerializedName("isSelected") var isSelected: Int = 0,
    val subBudgetPlanners: ArrayList<SubBudgetPlanner>

    ) : Serializable

data class SubBudgetPlanner(
    @SerializedName("budget_id") var budget_id: Int? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("createdAt") var createdAt: String? = null,
    @SerializedName("updatedAt") var updatedAt: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("isSubBudgetSelected") var isSubBudgetSelected: Int = 0,

): Serializable