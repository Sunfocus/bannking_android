package com.bannking.app.google_iab.models

import com.android.billingclient.api.AccountIdentifiers
import com.android.billingclient.api.Purchase
import com.bannking.app.google_iab.enums.SkuProductType

class PurchaseInfo(private val productInfo: ProductInfo, val purchase: Purchase) {
    val skuProductType: SkuProductType?
    val product: String?
    private val accountIdentifiers: AccountIdentifiers?
    private val products: List<String>
    private val orderId: String
    val purchaseToken: String
    val originalJson: String
    private val developerPayload: String
    private val packageName: String
    private val signature: String
    private val quantity: Int
    private val purchaseState: Int
    private val purchaseTime: Long
    private val isAcknowledged: Boolean
    private val isAutoRenewing: Boolean

    init {
        product = productInfo.product
        skuProductType = productInfo.skuProductType
        accountIdentifiers = purchase.accountIdentifiers
        products = purchase.products
        orderId = purchase.orderId!!
        purchaseToken = purchase.purchaseToken
        originalJson = purchase.originalJson
        developerPayload = purchase.developerPayload
        packageName = purchase.packageName
        signature = purchase.signature
        quantity = purchase.quantity
        purchaseState = purchase.purchaseState
        purchaseTime = purchase.purchaseTime
        isAcknowledged = purchase.isAcknowledged
        isAutoRenewing = purchase.isAutoRenewing
    }
}