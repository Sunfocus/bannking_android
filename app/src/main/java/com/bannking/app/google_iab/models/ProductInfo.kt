package com.bannking.app.google_iab.models

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetails.OneTimePurchaseOfferDetails
import com.android.billingclient.api.ProductDetails.SubscriptionOfferDetails
import com.bannking.app.google_iab.enums.SkuProductType

class ProductInfo(val skuProductType: SkuProductType, val productDetails: ProductDetails) {
    val product: String = productDetails.productId
    val description: String = productDetails.description
    val title: String = productDetails.title
    val type: String = productDetails.productType
    val name: String = productDetails.name
    private val oneTimePurchaseOfferDetails: OneTimePurchaseOfferDetails? =
        productDetails.oneTimePurchaseOfferDetails
    private val subscriptionOfferDetails: List<SubscriptionOfferDetails>? =
        productDetails.subscriptionOfferDetails

    val oneTimePurchaseOfferPrice: String
        get() = oneTimePurchaseOfferDetails!!.formattedPrice

    fun getSubscriptionOfferPrice(selectedOfferIndex: Int, selectedPricingPhaseIndex: Int): String {
        return subscriptionOfferDetails!![selectedOfferIndex].pricingPhases.pricingPhaseList[selectedPricingPhaseIndex].formattedPrice
    }
}