package com.bannking.app.model

data class GetTransactionResponse(
    val `data`: ArrayList<GetTransactionData>,
    val message: String,
    val status: Int
)

data class GetTransactionData(
    val amount: Double,
    val category: ArrayList<String>,
    val category_id: String,
    val current_balance: Double,
    val date: String,
    val transaction_id: String,
    val merchant_name: String,
    val name: String,
    val payment_meta: PaymentMeta
)

data class PaymentMeta(
    val by_order_of: Any,
    val payee: Any,
    val payer: Any,
    val payment_method: Any,
    val payment_processor: Any,
    val ppd_id: Any,
    val reason: Any,
    val reference_number: Any
)