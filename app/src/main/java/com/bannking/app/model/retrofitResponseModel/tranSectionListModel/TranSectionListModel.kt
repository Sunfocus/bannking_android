package com.bannking.app.model.retrofitResponseModel.tranSectionListModel

import com.google.gson.annotations.SerializedName


data class TranSectionListModel(

    @SerializedName("status") var status: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)

data class Data(

    @SerializedName("id") var id: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("type_name") var typeName: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("account_id") var accountId: String? = null,
    @SerializedName("account_title") var accountTitle: String? = null,
    @SerializedName("transaction_title") var transactionTitle: String? = null,
    @SerializedName("transaction_date") var transactionDate: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("currency_id") var currencyId: String? = null,
    @SerializedName("currency") var currency: String? = null,

    @SerializedName("orig_id") var origId: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("total_amount") var totalAmount: String? = null,
    @SerializedName("transaction_prefix") var transactionPrefix: String? = null,
    @SerializedName("is_orig") var isOrig: Boolean? = null,
    @SerializedName("is_pending") var is_pending: Boolean? = false

)