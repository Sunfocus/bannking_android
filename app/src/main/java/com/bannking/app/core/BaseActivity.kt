package com.bannking.app.core

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.bannking.app.model.retrofitResponseModel.userModel.Data
import com.bannking.app.utils.AdController
import com.bannking.app.utils.DialogClass
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.Utils
import com.google.gson.Gson
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity


abstract class BaseActivity<VM : BaseViewModel, Binding : ViewBinding?>(private val mViewModelClass: Class<VM>) :
    LocaleAwareCompatActivity() {


    @JvmField
    protected var binding: Binding? = null
    var gson: Gson = Gson()
    var utils: Utils = Utils()
    lateinit var sessionManager: SessionManager
    lateinit var inAppPurchaseSM: SessionManager
    lateinit var dialogClass: DialogClass

    companion object {
        //        var FCM_TOKEN = ""
        var userModel: Data? = null
        var isPremium: Boolean = false
    }


    private val viewModel by lazy {
        ViewModelProvider(this)[mViewModelClass]
//        ViewModelProviders.of(this)[mViewModelClass]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Get FCM_TOKEN
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

//        generateFCM(object : GetTokenFromOverride {
//            override fun sendToString(strToken : String) {
//                FCM_TOKEN = strToken
//                Log.e("FCM_TOKEN_RETURN", "FCM:-  $FCM_TOKEN")
//            }
//        })
        sessionManager = SessionManager(this@BaseActivity, SessionManager.mySharedPref)
        inAppPurchaseSM = SessionManager(this@BaseActivity, SessionManager.myInAppPurchase)
        dialogClass = DialogClass(this@BaseActivity)
        initBinding()
        setContentView(binding!!.root)
        initViewModel(viewModel)
        AdController.loadInterAd(this@BaseActivity)

        //initialize main here


        //Init
        initialize()
        //Use For Methods
        setMethod()
        //Use for ViewModelObserver
        observe()


    }

    protected abstract fun getBinding(): Binding

    abstract fun initViewModel(viewModel: VM)

    private fun initBinding() {
        binding = getBinding()
    }

    abstract fun initialize()

    protected abstract fun setMethod()

    abstract fun observe()


//    open fun generateFCM(getTokenFromOverride : GetTokenFromOverride) {
//        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("TAG_FCM", "Fetching FCM registration token failed", task.exception)
//                getTokenFromOverride.sendToString("null")
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//            getTokenFromOverride.sendToString(token)
//            Log.d("TAG_FCM_MY", token)
//        })
//
//    }

//    private fun generateFCM(getTokenFromOverride : GetTokenFromOverride) {
//        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("TAG_FCM", "Fetching FCM registration token failed", task.exception)
//                getTokenFromOverride.sendToString("null")
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//            getTokenFromOverride.sendToString(token)
//            Log.d("TAG_FCM_MY", token)
//        })
//
//    }

    open fun setUserDataInSession(data: Data) {
        sessionManager.setUserDetails(SessionManager.userData, data)
    }


    override fun onResume() {
        super.onResume()
        //get User Data
        if (sessionManager.getUserDetails(SessionManager.userData) != null) {
            userModel = sessionManager.getUserDetails(SessionManager.userData)!!
            Log.e("TAG_USER_MODEL", "onCreate: " + userModel!!.id)
        }
        isPremium = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
    }


}