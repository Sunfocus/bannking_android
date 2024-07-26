package com.bannking.app.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GooglePlayApiService {
    @GET("applications/{packageName}/purchases/subscriptions/{subscriptionId}/tokens/{token}")
    fun validatePurchaseToken(
        @Path("packageName") packageName: String,
        @Path("subscriptionId") subscriptionId: String,
        @Path("token") token: String,
//        @Header("Authorization") authHeader: String
    ): Call<GooglePlayResponse>
}

data class GooglePlayResponse(
    val startTimeMillis: Long,
    val expiryTimeMillis: Long,
    val autoRenewing: Boolean,
    val priceCurrencyCode: String,
    val priceAmountMicros: Long,
    val countryCode: String,
    val developerPayload: String?,
    val paymentState: Int,
    val cancelReason: Int?,
    val userCancellationTimeMillis: Long?,
    val orderId: String,
    val linkedPurchaseToken: String?,
    val purchaseType: Int?,
    val acknowledgementState: Int,
    val kind: String
)