package com.bannking.app.model

import com.google.gson.annotations.SerializedName

data class PayResponseApi (
    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: Data? = null
)
data class Data (
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("transaction_status") var transactionStatus: String? = null
)