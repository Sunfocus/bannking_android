package com.bannking.app.model

data class GetTransactionResponse(
    val `data`: GetTransactionData,
    val message: String,
    val status: Int
)

data class GetTransactionData(
    val new_transactions: ArrayList<NewTransaction>
)

data class NewTransaction(
    val amount: Double,
    val category: ArrayList<String>,
    val category_id: String,
    val date: String,
    val merchant_name: String,
    val name: String,
    val payment_meta: PaymentMeta,
    val current_balance:Double

)

class PaymentMeta