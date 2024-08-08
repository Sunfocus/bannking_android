package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.UiExtension.generateFCM
import com.bannking.app.UiExtension.getCurrentLanguage
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivitySplashBinding
import com.bannking.app.google_iab.models.PurchaseInfo
import com.bannking.app.model.retrofitResponseModel.appVersionModel.AppVersionModel
import com.bannking.app.model.retrofitResponseModel.premiumStatusModel.PremiumCheckModel
import com.bannking.app.model.viewModel.SplashViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.GetTokenFromOverride
import com.bannking.app.utils.SessionManager

@SuppressLint("CustomSplashScreen")
class SplashActivity :
    BaseActivity<SplashViewModel, ActivitySplashBinding>(SplashViewModel::class.java) {

    lateinit var viewModel: SplashViewModel
    private lateinit var checkPurchases: ArrayList<PurchaseInfo>

    companion object {
        private const val SPLASH_SCREEN_TIME_OUT = 1000
    }


    override fun getBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: SplashViewModel) {

        this.viewModel = viewModel
    }

    override fun initialize() {
        getCurrentLanguage()?.let { Log.e("getCurrentLanguage :", it.displayName) }
        checkPurchases = arrayListOf()
//        viewModel.setDataInAppVersionDataList()
//        if (sessionManager.getUserDetails(SessionManager.userData) != null) {
//            viewModel.setPremiumStatusDataList()
//        }

    }

    override fun setMethod() {

    }

    override fun observe() {
        with(viewModel) {
            if (sessionManager.getUserDetails(SessionManager.userData) != null) {
//                viewModel.setPremiumStatusDataList()
                passActivity()
            } else {
                passActivity()
            }
           /* appVersionDataList.observe(this@SplashActivity) { modelResponse ->
                if (modelResponse != null) {
                    if (modelResponse.code in 199..299) {
                        val model =
                            gson.fromJson(modelResponse.apiResponse, AppVersionModel::class.java)
                        if (model.data != null) {
                            if (model.data?.androidMaintenance == false) {
                                sessionManager.setString(SessionManager.APP_PRIVACY_POLICY, model.data!!.privacyUrl)
                                sessionManager.setString(
                                    SessionManager.AppVersion,
                                    model.data!!.androidVersion.toString()
                                )
                            } else {
                                dialogClass.showMaintenanceDialog()
                            }
                        }
                    }
                    if (sessionManager.getUserDetails(SessionManager.userData) != null) {
                        viewModel.setPremiumStatusDataList()
                    } else {
                        passActivity()
                    }
                }
            }*/
            getPremiumStatusDataList.observe(this@SplashActivity) { isPremium ->
                if (isPremium != null) {
                    if (isPremium.code in 199..299) {
                        val responseModel = gson.fromJson(
                            isPremium.apiResponse.toString(),
                            PremiumCheckModel::class.java
                        )
                        if (responseModel.status == 200) {
                            if (responseModel.isPremiumUser == true) {
                                inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                            } else if (responseModel.isPremiumUser == false) {
                                inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
                            }
                        }
                        passActivity()
                    }
                }
            }
        }
    }


    fun passActivity() {
        generateFCM(object : GetTokenFromOverride {
            override fun sendToString(strToken: String) {
                if (strToken.isEmpty()) {
                    passActivity()
                } else {
                    val lastActiveTime = sessionManager.getLastActiveTime()
                    val currentTime = System.currentTimeMillis()
                    sessionManager.setString("SetFcm", strToken)
                    val isLogin = sessionManager.getBoolean(SessionManager.isLogin)
                    FCM_TOKEN.let {
                        if (it?.isNotEmpty() == true) {
                            if (isLogin) {
                                if (currentTime - lastActiveTime > 3600000) { // 1 hour in milliseconds
                                    Handler().postDelayed({
                                        startActivity(Intent(this@SplashActivity, SignInActivity::class.java))
                                        finish()
                                    }, SPLASH_SCREEN_TIME_OUT.toLong())
                                } else {
                                    Handler().postDelayed({
                                        sessionManager.setBoolean(SessionManager.isDeleteORLogOut, false)
                                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                        finish()
                                    }, SPLASH_SCREEN_TIME_OUT.toLong())
                                }
                            } else {
                                Handler().postDelayed({
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            FirstScreenActivity::class.java
                                        )
                                    )
                                    finish()
                                }, SPLASH_SCREEN_TIME_OUT.toLong())
                            }
                        } else {
                            Log.e("TAG_FCM", "setMethod: Not Generated")
                        }
                    }
                }

            }
        })

    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

