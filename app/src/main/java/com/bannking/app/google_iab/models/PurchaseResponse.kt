package com.bannking.app.google_iab.models


import com.google.gson.annotations.SerializedName


data class PurchaseResponse(

    @SerializedName("orderId") var orderId: String? = null,
    @SerializedName("packageName") var packageName: String? = null,
    @SerializedName("productId") var productId: String? = null,
    @SerializedName("purchaseTime") var purchaseTime: Long? = null,
    @SerializedName("purchaseState") var purchaseState: Int? = null,
    @SerializedName("purchaseToken") var purchaseToken: String? = null,
    @SerializedName("obfuscatedAccountId") var obfuscatedAccountId: String? = null,
    @SerializedName("obfuscatedProfileId") var obfuscatedProfileId: String? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("autoRenewing") var autoRenewing: Boolean? = null,
    @SerializedName("acknowledged") var acknowledged: Boolean? = null

)