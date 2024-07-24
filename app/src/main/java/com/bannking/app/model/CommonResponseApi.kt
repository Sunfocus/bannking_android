package com.bannking.app.model

import com.google.gson.annotations.SerializedName

data class CommonResponseApi(

    @SerializedName("status") var status: String? = null,
    @SerializedName("transaction_status") var transactionStatus: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("user_id") var user_id: String? = null

)