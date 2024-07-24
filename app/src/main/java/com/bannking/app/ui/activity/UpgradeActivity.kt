package com.bannking.app.ui.activity

import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityUpgradeNewBinding
import com.bannking.app.google_iab.BillingConnector
import com.bannking.app.google_iab.BillingEventListener
import com.bannking.app.google_iab.enums.ErrorType
import com.bannking.app.google_iab.enums.ProductType
import com.bannking.app.google_iab.models.BillingResponse
import com.bannking.app.google_iab.models.ProductInfo
import com.bannking.app.google_iab.models.PurchaseInfo
import com.bannking.app.google_iab.models.PurchaseResponse
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.viewModel.UpgradeViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.OnClickListenerViewPager
import com.bannking.app.utils.SessionManager
import com.google.android.material.tabs.TabLayoutMediator

/**       checkPurchases = arrayListOf()
Subscription Means Subscribe
Non Consumable Means One Time Purchase
Consumable Means Many Time Purchase */

class UpgradeActivity :
    BaseActivity<UpgradeViewModel, ActivityUpgradeNewBinding>(UpgradeViewModel::class.java),
    OnClickListenerViewPager {

    private var mediator: TabLayoutMediator? = null
    lateinit var viewModel: UpgradeViewModel
    private var clickedItemSUb = Constants.three_month_subscription_id

    private lateinit var billingConnector: BillingConnector

    override fun getBinding(): ActivityUpgradeNewBinding {
        return ActivityUpgradeNewBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: UpgradeViewModel) {
        this.viewModel = viewModel
    }

    override fun initialize() {}

    override fun setMethod() {
        initializeBillingClient()
//        setViewPager()
        setOnClickListener()
    }

    override fun observe() {
        with(viewModel) {
            subscription.observe(this@UpgradeActivity) { checkSubscription ->
                if (checkSubscription != null) {
                    if (checkSubscription.code in 199..299) {
                        val model = gson.fromJson(
                            checkSubscription.apiResponse, CommonResponseApi::class.java
                        )
                        if (model.status.equals(Constants.STATUSSUCCESS, true)) {
//                            Toast.makeText(
//                                this@UpgradeActivity,
//                                userModel?.username + "Purchase Successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                            finish()
                        } else {
                            dialogClass.showError(resources.getString(R.string.str_server_error))
//                            Toast.makeText(this@UpgradeActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        dialogClass.showError(resources.getString(R.string.str_server_error))
//                        Toast.makeText(this@UpgradeActivity, "Something went wrong Please Contact app admin ", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            //Amit
            subscriptionNew.observe(this@UpgradeActivity) { checkSubscription ->
                if (checkSubscription != null) {
                    if (checkSubscription.code in 199..299) {
                        val model = gson.fromJson(
                            checkSubscription.apiResponse, CommonResponseApi::class.java
                        )
                        if (model.status.equals(Constants.STATUSSUCCESS, true)) {
//                            Toast.makeText(
//                                this@UpgradeActivity,
//                                userModel?.username + "Purchase Successfully",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                            finish()
                        } else {
                            dialogClass.showError(resources.getString(R.string.str_server_error))
//                            Toast.makeText(this@UpgradeActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        dialogClass.showError(resources.getString(R.string.str_server_error))
//                        Toast.makeText(this@UpgradeActivity, "Something went wrong Please Contact app admin ", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            progressObservable.observe(this@UpgradeActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }

    }

    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener { finish() }

            /*           llOneMonth.setOnClickListener {
                           billingConnector.subscribe(
                               this@UpgradeActivity, Constants.one_month_subscription_id
                           )
                       }*/


            llOneMonth.setOnClickListener {
                btnThreeDayTrial.text = resources.getString(R.string._3_days_free_trial)

                clickedItemSUb = Constants.one_month_subscription_id
                llOneMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.background_gradiant_subs
                )
                llThreeMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llSixMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llLifeTime.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
//                billingConnector.subscribe(
//                    this@UpgradeActivity, Constants.one_month_subscription_id
//                )
            }

            llThreeMonth.setOnClickListener {
                btnThreeDayTrial.text = resources.getString(R.string._3_days_free_trial)

                clickedItemSUb = Constants.three_month_subscription_id
                llThreeMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.background_gradiant_subs
                )
                llOneMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llSixMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llLifeTime.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )/*  billingConnector.subscribe(
                      this@UpgradeActivity, Constants.three_month_subscription_id
                  )*/
            }

            /*      btnBuyThree.setOnClickListener {
                billingConnector.subscribe(
                    this@UpgradeActivity, Constants.three_month_subscription_id
                )
            }*/

            llSixMonth.setOnClickListener {
                btnThreeDayTrial.text = resources.getString(R.string._3_days_free_trial)
                clickedItemSUb = Constants.six_month_subscription_id
                llSixMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.background_gradiant_subs
                )
                llOneMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llThreeMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llLifeTime.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )


                /* billingConnector.subscribe(
                     this@UpgradeActivity, Constants.six_month_subscription_id
                 )*/
            }

            /*            btnBuySix.setOnClickListener {
                billingConnector.subscribe(
                    this@UpgradeActivity, Constants.six_month_subscription_id
                )
            }*/

            btnThreeDayTrial.setOnClickListener {
                Log.d("dfgdfgfdgfdg",clickedItemSUb)
                if (clickedItemSUb == Constants.life_time_subscription_id) {
                    billingConnector.purchase(
                        this@UpgradeActivity,
                        Constants.life_time_subscription_id
                    )

                } else if (clickedItemSUb.isNotEmpty()) {
                    billingConnector.subscribe(
                        this@UpgradeActivity, clickedItemSUb
                    )
                } else {
                    Toast.makeText(
                        this@UpgradeActivity, "Please select one of them", Toast.LENGTH_SHORT
                    ).show()
                }
                Log.d(
                    "clickedItemSUb", clickedItemSUb
                )

            }

            llLifeTime.setOnClickListener {
                btnThreeDayTrial.text = "Continue"
                clickedItemSUb = Constants.life_time_subscription_id
                llLifeTime.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.background_gradiant_subs
                )
                llThreeMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llSixMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
                llOneMonth.background = ContextCompat.getDrawable(
                    this@UpgradeActivity, R.drawable.shape_upgrade_background
                )
//                billingConnector.purchase(this@UpgradeActivity, Constants.life_time_subscription_id)
            }

            /*         btnBuyLife.setOnClickListener {
                             billingConnector.purchase(this@UpgradeActivity, Constants.life_time_subscription_id)
                         }*/
        }
    }

    private fun setViewPager() {

//        if (purchaseId == Constants.life_time_subscription_id) {
//            billingConnector.purchase(this, purchaseId)
//        } else {
//            billingConnector.subscribe(this, purchaseId)
//        }

        with(binding!!) {
//            imgOneMonth.setOnClickListener{
//                billingConnector.subscribe(this@UpgradeActivity,  Constants.one_month_subscription_id)
//            }
//
//            imgThreeMonth.setOnClickListener{
//                billingConnector.subscribe(this@UpgradeActivity,  Constants.three_month_subscription_id)
//            }
//
//            imgSixMonth.setOnClickListener{
//                billingConnector.subscribe(this@UpgradeActivity,  Constants.six_month_subscription_id)
//            }
//
//            imgLifetimeMonth.setOnClickListener{
//                billingConnector.purchase(this@UpgradeActivity,  Constants.life_time_subscription_id)
//            }
        }
//        val upgradeSliderItems: MutableList<UpgradeSliderModel> = ArrayList()
//        upgradeSliderItems.add(
//            UpgradeSliderModel(
//                R.drawable.ic_1_month,
//                Constants.one_month_subscription_id
//            )
//        )
//        upgradeSliderItems.add(
//            UpgradeSliderModel(
//                R.drawable.ic_3_month,
//                Constants.three_month_subscription_id
//            )
//        )
//        upgradeSliderItems.add(
//            UpgradeSliderModel(
//                R.drawable.ic_6_month,
//                Constants.six_month_subscription_id
//            )
//        )
//        upgradeSliderItems.add(
//            UpgradeSliderModel(
//                R.drawable.ic_lifetime,
//                Constants.life_time_subscription_id
//            )
//        )

//        binding!!.viewPagerImageSlider.adapter =
//            SliderAdapter(upgradeSliderItems, binding!!.viewPagerImageSlider, this)
//        mediator = TabLayoutMediator(
//            binding!!.intoTabLayout, binding!!.viewPagerImageSlider, true
//        ) { _, _ -> }
//
//        mediator!!.attach()
//        binding!!.viewPagerImageSlider.clipToPadding = false
//        binding!!.viewPagerImageSlider.clipChildren = false
//        binding!!.viewPagerImageSlider.offscreenPageLimit = 3
//        binding!!.viewPagerImageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//        val compositePageTransformer = CompositePageTransformer()
//        compositePageTransformer.addTransformer(MarginPageTransformer(40))
//        compositePageTransformer.addTransformer { page, position ->
//            val r = 1 - abs(position)
//            page.scaleY = 0.85f + r * 0.15f
//        }
//        binding!!.viewPagerImageSlider.setPageTransformer(compositePageTransformer)
//        binding!!.viewPagerImageSlider.registerOnPageChangeCallback(object :
//            OnPageChangeCallback() {
//        })
    }


    //RecyclerView Click
    override fun onClickListener(purchaseId: String) {
        if (purchaseId == Constants.life_time_subscription_id) {
            billingConnector.purchase(this, purchaseId)
        } else {
            billingConnector.subscribe(this, purchaseId)
        }
    }

    private fun initializeBillingClient() {
        //create a list with consumable ids
        val consumableIds = mutableListOf<String>()
//        consumableIds.add("life_time_subscription_id")
//        checkPurchases = arrayListOf()
//        Subscription Means Subscribe
//        Non Consumable Means One Time Purchase
//        Consumable Means Many Time Purchase

        //create a list with non-consumable ids
        val nonConsumableIds = mutableListOf<String>()
        nonConsumableIds.add(Constants.life_time_subscription_id)

        //create a list with subscription ids
        val subscriptionIds = mutableListOf<String>()
        subscriptionIds.add(Constants.one_month_subscription_id)
        subscriptionIds.add(Constants.three_month_subscription_id)
        subscriptionIds.add(Constants.six_month_subscription_id)

        billingConnector = BillingConnector(
            this, Constants.LICENSE_KEY
        ) //"license_key" - public developer key from Play Console
            .setConsumableIds(consumableIds) //to set consumable ids - call only for consumable products
            .setNonConsumableIds(nonConsumableIds) //to set non-consumable ids - call only for non-consumable products
            .setSubscriptionIds(subscriptionIds) //to set subscription ids - call only for subscription products
            .autoAcknowledge() //legacy option - better call this. Alternatively purchases can be acknowledge via public method "acknowledgePurchase(PurchaseInfo purchaseInfo)"
            .autoConsume() //legacy option - better call this. Alternatively purchases can be consumed via public method consumePurchase(PurchaseInfo purchaseInfo)"
            .enableLogging() //to enable logging for debugging throughout the library - this can be skipped
            .connect() //to connect billing client with Play Console

        billingConnector.setBillingEventListener(object : BillingEventListener {

            override fun onProductsFetched(productDetails: List<ProductInfo?>) {
                if (productDetails.size == 1) {
                    if ((productDetails[0]?.productDetails?.productType ?: "") == "inapp") {
                        productDetails[0]?.oneTimePurchaseOfferPrice.toString().let {
                            if (it.isNotEmpty() && it != "null") {
                                binding!!.txtLifetimeAmount.text = it
                                Log.e("MyAp_life", "Life $it")
                            }
                        }
                    }

                } else {
                    for (i in productDetails.indices) {
                        if ((productDetails[i]?.productDetails?.productType ?: "") == "subs") {
                            Log.d(
                                "dsjfdhdsjkfsd",
                                productDetails[i]?.productDetails?.subscriptionOfferDetails!!.toString()
                            )
                            //amit
                            if (productDetails[i]?.productDetails?.subscriptionOfferDetails!!.size >= 2) {
                                productDetails[i]?.productDetails?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    1
                                )?.formattedPrice.toString().let {
                                    if (it.isNotEmpty() && it != "null") {
                                        when (productDetails[i]?.productDetails?.productId) {
                                            Constants.one_month_subscription_id -> {
                                                binding!!.txtOneMonthAmount.text = it
                                            }

                                            Constants.three_month_subscription_id -> {
                                                binding!!.txtThreeMonthAmount.text = it
                                            }

                                            Constants.six_month_subscription_id -> {
                                                binding!!.txtSixMonthAmount.text = it
                                            }
                                        }
                                        Log.e("MyAp_sub", "indx $i plan $it ")
                                    }

                                }
                            } else {
                                productDetails[i]?.productDetails?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    0
                                )?.formattedPrice.toString().let {
                                    if (it.isNotEmpty() && it != "null") {
                                        when (productDetails[i]?.productDetails?.productId) {
                                            Constants.one_month_subscription_id -> {
                                                binding!!.txtOneMonthAmount.text = it
                                            }

                                            Constants.three_month_subscription_id -> {
                                                binding!!.txtThreeMonthAmount.text = it
                                            }

                                            Constants.six_month_subscription_id -> {
                                                binding!!.txtSixMonthAmount.text = it
                                            }
                                        }
                                        Log.e("MyAp_sub", "indx $i plan $it ")
                                    }

                                }
                            }

                        }
                    }
                }
            }

            override fun onPurchasedProductsFetched(
                productType: ProductType, purchases: List<PurchaseInfo?>
            ) {
//                Log.e("onPurchasedProductsFetched", "onPurchasedProductsFetched: $i ")
//                i++
//                checkPurchases.addAll(purchases)
                purchases.forEach {
                    when (productType) {
                        ProductType.INAPP -> {
                            inAppPurchaseSM.setString(SessionManager.purchaseId, it?.product)
                        }

                        ProductType.SUBS -> {
                            inAppPurchaseSM.setString(SessionManager.purchaseId, it?.product)
                        }

                        ProductType.COMBINED -> {}
                    }
                }
//                if (i == 3) {
//                    if (checkPurchases.isEmpty()) {
//                        inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
//                    } else {
//                        utils.removeDuplicates(checkPurchases)?.forEach { purchasesObject ->
//                            val purchaseResponse = gson.fromJson(purchasesObject.originalJson, PurchaseResponse::class.java)
//                            if (purchaseResponse.obfuscatedProfileId == userModel?.id.toString()) {
//                                inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
//                            } else {
//                                Toast.makeText(this@UpgradeActivity, "Purchase Another User " + "Subscription In your play store " + "account please Check and change", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                    }
//                }
            }

            override fun onProductsPurchased(purchases: List<PurchaseInfo?>) {
                var product: String
                var purchaseToken: String


                for (purchaseInfo in purchases) {
                    val purchaseResponse =
                        gson.fromJson(purchaseInfo?.originalJson, PurchaseResponse::class.java)
                    purchaseResponse.productId?.let {
                        purchaseResponse.purchaseToken?.let { it1 ->
                            inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                            if (it == Constants.life_time_subscription_id){
                                purchaseResponse.purchaseTime?.let { it2 ->
                                    viewModel.setDataInAccountTitleList(
                                        it1, it, it2, "1"
                                    )
                                }
                            }else{
                                //amit
                                Log.d("dsfdsfdsfdsf",it)
                                purchaseResponse.purchaseTime?.let { it2 ->
                                    viewModel.setDataInAccountTitleListNew(
                                        it1, it, it2, "1"
                                    )
                                }

                            }
                        }
                    }
                    product = purchaseInfo?.product.toString()
                    purchaseToken = purchaseInfo?.purchaseToken.toString()

                    if (product.equals("subscription_id_3", ignoreCase = true)) {
                        //TODO - do something
                        Log.d("BillingConnector", "Product purchased: $product")
//                        Toast.makeText(
//                            this@UpgradeActivity,
//                            "Product purchased: $product",
//                            Toast.LENGTH_SHORT
//                        ).show()

                        //TODO - do something
                        Log.d("BillingConnector", "Purchase token: $purchaseToken")
//                        Toast.makeText(
//                            this@UpgradeActivity,
//                            "Purchase token: $purchaseToken",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }

                    //TODO - similarly check for other ids

                }
            }

            override fun onPurchaseAcknowledged(purchase: PurchaseInfo) {
                when (purchase.product) {
                    "non_consumable_id_2" -> {
                        //TODO - do something
                        Log.d("BillingConnector", "Acknowledged: ${purchase.product}")
//                        Toast.makeText(
//                            this@UpgradeActivity,
//                            "Acknowledged: ${purchase.product}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }

                    //TODO - similarly check for other ids
                }
            }

            override fun onPurchaseConsumed(purchase: PurchaseInfo) {
                when (purchase.product) {
                    "theree_month_subscription_id" -> {
                        //TODO - do something
                        Log.d("BillingConnector", "Consumed: ${purchase.product}")
//                        Toast.makeText(
//                            this@UpgradeActivity,
//                            "Consumed: ${purchase.product}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }

                    //TODO - similarly check for other ids
                }
            }

            override fun onBillingError(
                billingConnector: BillingConnector, response: BillingResponse
            ) {
                when (response.errorType) {
                    ErrorType.CLIENT_NOT_READY -> {}
                    ErrorType.CLIENT_DISCONNECTED -> {
                        //TODO - client has disconnected
                    }

                    ErrorType.PRODUCT_NOT_EXIST -> {
                        //TODO - product does not exist
                    }

                    ErrorType.CONSUME_ERROR -> {
                        //TODO - error during consumption
                    }

                    ErrorType.CONSUME_WARNING -> {/*
                        * This will be triggered when a consumable purchase has a PENDING state
                        * User entitlement must be granted when the state is PURCHASED
                        *
                        * PENDING transactions usually occur when users choose cash as their form of payment
                        *
                        * Here users can be informed that it may take a while until the purchase complete
                        * and to come back later to receive their purchase
                        * */
                        //TODO - warning during consumption
                    }

                    ErrorType.ACKNOWLEDGE_ERROR -> {
                        //TODO - error during acknowledgment
                    }

                    ErrorType.ACKNOWLEDGE_WARNING -> {/*
                          * This will be triggered when a purchase can not be acknowledged because the state is PENDING
                          * A purchase can be acknowledged only when the state is PURCHASED
                          *
                          * PENDING transactions usually occur when users choose cash as their form of payment
                          *
                          * Here users can be informed that it may take a while until the purchase complete
                          * and to come back later to receive their purchase
                          * */
                        //TODO - warning during acknowledgment
                    }

                    ErrorType.FETCH_PURCHASED_PRODUCTS_ERROR -> {
                        //TODO - error occurred while querying purchased products
                    }

                    ErrorType.BILLING_ERROR -> {
                        //TODO - error occurred during initialization / querying product details
                    }

                    ErrorType.USER_CANCELED -> {
                        //TODO - user pressed back or canceled a dialog
                    }

                    ErrorType.SERVICE_UNAVAILABLE -> {
                        //TODO - network connection is down
                    }

                    ErrorType.BILLING_UNAVAILABLE -> {
                        //TODO - billing API version is not supported for the type requested
                    }

                    ErrorType.ITEM_UNAVAILABLE -> {
                        //TODO - requested product is not available for purchase
                    }

                    ErrorType.DEVELOPER_ERROR -> {
                        //TODO - invalid arguments provided to the API
                    }

                    ErrorType.ERROR -> {
                        //TODO - fatal error during the API action
                    }

                    ErrorType.ITEM_ALREADY_OWNED -> {
                        //TODO - item is already owned
                    }

                    ErrorType.ITEM_NOT_OWNED -> {
                        //TODO - failure to consume since item is not owned
                    }
                }

                Log.d(
                    "BillingConnector",
                    "Error type: ${response.errorType}" + " Response code: ${response.responseCode}" + " Message: ${response.debugMessage}"
                )

//                Toast.makeText(
//                    this@UpgradeActivity,
//                    "Error type: ${response.errorType}" + " Response code: ${response.responseCode}"
//                            + " Message: ${response.debugMessage}",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        })
    }


}