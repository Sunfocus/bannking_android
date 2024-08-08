package com.bannking.app.model.retrofitResponseModel.tranSectionListModel


import com.google.gson.annotations.SerializedName


data class TranSectionListModel(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("extraData") var extraData: String? = null

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

    @SerializedName("account_data") var account_data: AccountData? = null,

    @SerializedName("orig_id") var origId: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("final_amount_account") var totalAmount: String? = null,
    @SerializedName("transactionPrefix") var transactionPrefix: String? = null,
    @SerializedName("is_orig") var isOrig: Boolean? = null,
    @SerializedName("is_pending") var is_pending: Boolean? = false,
    @SerializedName("cron_status") var cron_status: Int? = 0

)

data class Currency(
    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("icon") var icon: String? = null,

    )
data class AccountData(
    @SerializedName("currency") var currency: Currency? = null

    )